defmodule ChatGatewayWeb.Router do
  use ChatGatewayWeb, :router

  pipeline :browser do
    plug :accepts, ["html"]
    plug :fetch_session
    plug :fetch_flash
    plug :protect_from_forgery
    plug :put_secure_browser_headers
  end

  pipeline :api do
    plug :accepts, ["json"]
  end

  scope "/", ChatGatewayWeb do
    # This is a WebSocket-only service, so no browser routes are needed.
    # If you wanted to add a health check endpoint, you could add it here.
    #
    # pipeline :api do
    #   get "/health", HealthController, :check
    # end
  end
end
