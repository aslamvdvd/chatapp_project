defmodule ChatGatewayWeb.UserSocket do
  use Phoenix.Socket

  alias ChatGateway.Auth.JWTAuth

  ## Channels
  # Topics are subscribed to using the `channel` transport.
  channel "user:*", ChatGatewayWeb.UserChannel
  channel "group:*", ChatGatewayWeb.GroupChannel
  channel "channel:*", ChatGatewayWeb.ChannelChannel

  @doc """
  The `connect/3` function is the first function called when a client
  attempts to connect to the socket. It is responsible for authenticating
  the connection and assigning the user's ID to the socket.
  """
  @impl true
  def connect(%{"token" => token}, socket, _connect_info) do
    # In a production app, you would have a more robust auth flow,
    # possibly involving a GenServer to cache user data.
    with {:ok, claims} <- JWTAuth.verify(token),
         # The "sub" claim typically holds the user ID.
         %{"sub" => user_id} <- claims do
      socket = assign(socket, :user_id, user_id)
      {:ok, socket}
    else
      # We can't connect if the token is invalid or missing the user ID.
      _error -> :error
    end
  end

  # This is called if the token is missing from the connection params.
  def connect(_params, _socket, _connect_info), do: :error

  @doc """
  The `id/1` function is used to uniquely identify a socket connection.
  This is useful for tracking presence and for routing messages directly
  to a specific user's process. Here we use the user's ID.
  """
  @impl true
  def id(socket), do: "user_socket:#{socket.assigns.user_id}"
end 