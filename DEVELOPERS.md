# Developers
useful tips for development and its environment.

## Setup

How your local environment needs to be prepared to start developing.

### Environment

#### Lokalise
create a read-only api token under `https://lokalise.co/profile/#apitokens`.
add to local.properties
```
lokalise.token=<token>
```

execute `gradlew downloadTranslations" to get new translations