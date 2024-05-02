# Discord Support Bot

## Overview
Discord bot built with Spring Boot and Discord4J to support closed community activities such as music streaming or storing notable messages.

## Features
- **Music Streaming:** Stream music directly into Discord voice channels.
- **Message Pinning:** (Planned) Pin messages to a separate channel to bypass the 50-pin limit per channel.
- **External API Integration:** (Planned) Allow external control of the bot through APIs.

## Getting Started

### Prerequisites
- Java JDK 11 or newer
- Maven
- Discord account and a bot token


### Setup
1. Create a Discord App [following step 1 here.](https://discord.com/developers/docs/quick-start/getting-started#step-1-creating-an-app)
    - You'll need the bot token so keep it safe!

### Running locally
2. Build the project using Maven:
```bash
mvn clean install
```
3. Set up environment variables for `DISCORD_BOT_TOKEN` and optionally `LOG_DIR` for logging:
    - Use your bot token here
```bash
export DISCORD_BOT_TOKEN='your_bot_token_here'
export LOG_DIR='/path/to/logs'
```
4. Run the bot using:
```bash
java -jar target/spring-bot-v0.1.1.jar
```
### Docker Deployment (Recommended)
Alternatively, you can use Docker to build and run the bot:

2. Pull the image from Docker Hub (you can check it out [here](https://hub.docker.com/r/lhinh/spring-bot))
```bash
docker pull lhinh/spring-bot
```
3. Run the container using the pulled image:
    - Use your bot token here
```bash
docker run -d -e DISCORD_BOT_TOKEN='your_bot_token_here' -v /path/to/logs:/app/logs lhinh/spring-bot
```
## Commands
The bot supports several commands for interacting with Discord:
- `/ping`: Returns the guild ID of the server.
- `/join`: Joins the voice channel that the user is currently in.
- `/play [link]`: Plays the audio from the provided link.
- `/disconnect`: Disconnects the bot from the voice channel.
- `/stop`: Stops the current audio playback and clears the playlist.
- `/skip`: Skips to the next track in the playlist.
