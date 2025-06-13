defmodule ChatGatewayWeb.ChannelChannel do
  use Phoenix.Channel
  alias ChatGatewayWeb.Endpoint

  @impl true
  def join("channel:" <> _channel_id, _payload, socket) do
    # Any authenticated user can join a public channel to listen for messages.
    {:ok, socket}
  end

  # Only authorized users (e.g., admins) can broadcast messages to a channel.
  @impl true
  def handle_in("new_msg", payload, socket) do
    if admin?(socket) do
      # `broadcast` sends to all clients subscribed to the topic, including the sender.
      Endpoint.broadcast(socket.topic, "new_msg", payload)
      {:noreply, socket}
    else
      # Respond to the sender with an error if they are not authorized.
      push(socket, "error", %{reason: "unauthorized"})
      {:noreply, socket}
    end
  end

  # A simple placeholder for an admin check.
  # In a real app, this would involve checking the user's roles,
  # which would likely be part of the JWT claims.
  defp admin?(socket) do
    # For now, we can check for a specific user ID or a claim in the JWT.
    # Let's assume an "is_admin" claim for demonstration.
    socket.assigns[:is_admin] == true
  end
end 