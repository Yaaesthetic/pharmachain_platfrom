"use server"

// This prevents exposing the client secret to the client-side code

export async function authenticateWithKeycloak(username: string, password: string) {
  const tokenUrl =
    process.env.KEYCLOAK_TOKEN_URL
  const clientId = process.env.KEYCLOAK_CLIENT_ID
  const clientSecret = process.env.KEYCLOAK_CLIENT_SECRET

  try {
    const response = await fetch(tokenUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        client_id: clientId,
        client_secret: clientSecret,
        grant_type: "password",
        username,
        password,
        scope: "openid profile email",
      }),
    })

    if (!response.ok) {
      const error = await response.json()
      throw new Error(error.error_description || "Invalid credentials")
    }

    const tokens = await response.json()
    return tokens
  } catch (error: any) {
    console.error("[v0] Keycloak authentication error:", error)
    throw new Error(error.message || "Authentication failed")
  }
}
