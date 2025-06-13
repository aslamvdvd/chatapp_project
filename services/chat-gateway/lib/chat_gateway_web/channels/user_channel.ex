defmodule ChatGatewayWeb.UserChannel do
  use Phoenix.Channel
  alias ChatGatewayWeb.Endpoint
  alias ChatGateway.RateLimiter

  @impl true
  def join("user:" <> user_id, _payload, socket) do
    # A user can only join their own user channel.
    if socket.assigns.user_id == user_id do
      {:ok, socket}
    else
      {:error, %{reason: "unauthorized"}}
    end
  end

  # This is called when a client pushes a "new_msg" event.
  # It broadcasts the message to the recipient's user channel.
  @impl true
  def handle_in("new_msg", payload, socket) do
    # Rate limit to 5 messages every 10 seconds per user.
    with :ok <- RateLimiter.check_rate(socket.assigns.user_id, :new_msg, 5, 10) do
      # In a real app, you would persist the message to a database here.
      # We are assuming the payload contains the recipient's ID.
      recipient_topic = "user:#{payload["recipient_id"]}"
      Endpoint.broadcast(recipient_topic, "new_msg", payload)
      {:noreply, socket}
    else
      {:error, reason} ->
        # Push an error event back to the client if they are rate-limited.
        push(socket, "error", %{reason: reason})
        {:noreply, socket}
    end
  end

  # This handles typing notifications.
  @impl true
  def handle_in("typing", payload, socket) do
    recipient_topic = "user:#{payload["recipient_id"]}"
    Endpoint.broadcast(recipient_topic, "typing", %{user_id: socket.assigns.user_id})
    {:noreply, socket}
  end

  # Handles message receipts.
  @impl true
  def handle_in("delivered", payload, socket) do
    sender_topic = "user:#{payload["sender_id"]}"
    Endpoint.broadcast(sender_topic, "delivered", %{message_id: payload["message_id"]})
    {:noreply, socket}
  end

  @impl true
  def handle_in("seen", payload, socket) do
    sender_topic = "user:#{payload["sender_id"]}"
    Endpoint.broadcast(sender_topic, "seen", %{message_id: payload["message_id"]})
    {:noreply, socket}
  end
end 