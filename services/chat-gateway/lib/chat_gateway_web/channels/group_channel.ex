defmodule ChatGatewayWeb.GroupChannel do
  use Phoenix.Channel
  alias ChatGatewayWeb.Endpoint

  @impl true
  def join("group:" <> _group_id, _payload, socket) do
    # In a real app, you would verify that the user is a member of the group
    # before allowing them to join the channel. This would likely involve
    # a call to another service (e.g., a user management service).
    # For now, we'll allow any authenticated user to join.
    {:ok, socket}
  end

  # This is called when a client pushes a "new_msg" event to a group.
  # It broadcasts the message to all other members of the group channel.
  @impl true
  def handle_in("new_msg", payload, socket) do
    # The message is broadcast to all clients subscribed to the group topic.
    # The `broadcast_from` function ensures that the sender does not
    # receive a copy of their own message.
    broadcast_from socket, "new_msg", payload
    {:noreply, socket}
  end

  # This handles typing notifications for the group.
  @impl true
  def handle_in("typing", payload, socket) do
    broadcast_from socket, "typing", %{user_id: socket.assigns.user_id, room_id: payload["room_id"]}
    {:noreply, socket}
  end

  @impl true
  def handle_in("stop_typing", payload, socket) do
    broadcast_from socket, "stop_typing", %{user_id: socket.assigns.user_id, room_id: payload["room_id"]}
    {:noreply, socket}
  end
end 