defmodule ChatGateway.RateLimiter do
  use GenServer
  require Logger

  @table_name :rate_limit_table

  # Public API
  def start_link(_opts) do
    GenServer.start_link(__MODULE__, [], name: __MODULE__)
  end

  def check_rate(user_id, event_type, limit, window_seconds) do
    GenServer.call(__MODULE__, {:check_rate, user_id, event_type, limit, window_seconds})
  end

  # GenServer Callbacks
  @impl true
  def init(_) do
    @table_name = :ets.new(@table_name, [:set, :public, :named_table])
    {:ok, %{}}
  end

  @impl true
  def handle_call({:check_rate, user_id, event_type, limit, window_seconds}, _from, state) do
    key = {user_id, event_type}
    now = :os.system_time(:millisecond)
    window_start = now - (window_seconds * 1000)

    # Get timestamps for the user and event, filter out old ones
    timestamps =
      case :ets.lookup(@table_name, key) do
        [{^key, ts_list}] -> Enum.filter(ts_list, fn ts -> ts > window_start end)
        [] -> []
      end

    if length(timestamps) < limit do
      :ets.insert(@table_name, {key, [now | timestamps]})
      {:reply, :ok, state}
    else
      Logger.warn("Rate limit exceeded for user #{user_id} on event #{event_type}")
      {:reply, {:error, :rate_limit_exceeded}, state}
    end
  end
end 