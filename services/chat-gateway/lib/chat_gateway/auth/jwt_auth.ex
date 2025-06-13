defmodule ChatGateway.Auth.JWTAuth do
  @moduledoc """
  Verifies JWTs for socket connections using Joken.
  It reads the JWT secret from the application environment.
  """
  use Joken.Config

  @impl true
  def token_config do
    # Get the secret from the application environment.
    # This is configured in `config/config.exs`.
    secret = Application.fetch_env!(:chat_gateway, :jwt_secret)

    default_signer = Joken.Signer.create("HS256", secret)

    %{"default" => default_signer}
  end

  @doc """
  Verifies the token and returns the claims on success.
  """
  def verify(token) do
    Joken.verify_and_validate(token)
  end
end 