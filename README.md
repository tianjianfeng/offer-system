# Offer System

The application is implemented using TDD together with some integration test.

## Assumptions:

The OfferRepoImpl is implemented using map for simplicity. But in reality, it could be a database implementation.

The OfferService layer works as the business logic layer, but for this implementation it only proxy through the repo calls.

The system only allow to create an offer which expireDate has to be more than 0.5 days and beyond

## Test

```bash
sbt test
```

## Run

```bash
sbt run
```

