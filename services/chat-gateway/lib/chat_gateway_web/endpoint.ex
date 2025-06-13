defmodule ChatGatewayWeb.Endpoint do
  use Phoenix.Endpoint, otp_app: :chat_gateway

  # The session will be used for web authentication
  @session_options [
    store: :cookie,
    key: "_chat_gateway_key",
    signing_salt: "some salt",
    same_site: "Lax"
  ]

  socket "/socket", ChatGatewayWeb.UserSocket,
    websocket: true,
    longpoll: false

  # Serve at "/" the static files from "priv/static" directory.
  #
  plug Plug.Parsers,
    parsers: [:json],
    pass: ["*/*"],
    json_decoder: Phoenix.json_library()

  plug Plug.MethodOverride
  plug Plug.Head
  # The session plug is used for web authentication.
  # If you are building a purely API-driven app, you may not need it.
  # plug Plug.Session, @session_options
  plug ChatGatewayWeb.Router
end 