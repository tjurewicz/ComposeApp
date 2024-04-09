A vertical slice of an app I worked on for a few months, using Jetpack Compose, MVVM + Clean Architecture, Koin, Coroutines, Realm.

You should be able to build the app using `gradle build`.

The app allows you to track the CO2 emissions of your diet. Simply add the groceries you bought during your trip to the supermarket, and your CO2 emissions for those items will be calculated.

Features:
1. Login / Sign up
2. Overview screen, showing your weekly CO2 budget and a list of shopping trips you've added
3. Logging groceries: Searching a list of items, adding items to a basket, editing the basket, purchase summary screen
4. Persistent storage using Realm
