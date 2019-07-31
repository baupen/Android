# Architecture

As there seems to be no "standard" way an android application is made, we had to take some architecture decisions. 

in short:
`activities` use `viewModels` to display data from `repositories` read out from `services`. 

## activities

... coordinate what is displayed on the view and perform the actions of the user.

the activity gets the data from the repositories. 

the activity reacts to relevant events and informs the user about it.
for example, it may displays an error message if a specific request has failed or refreshes the displayed data if it has changed.

each acitivity has its own `viewModel` which displays the data.  
if the user performs an action which has effects besides altering the UI, the viewModel calls a method of the activity.  
each viewModel has therefore three arguments: the `View` it alters, the `Payload` with the data it displayed, and the `T` it relays actions of the user.

## apis

... exactly mirrors the way the API is set up.

it contains all the entites, requests & responses from the API and provides background tasks to execute these requests.

## events

... comunicate when background tasks have relevant results for its consumers.

`Saved` events are published when the database is changed outside of the authoritative repository. 
For example, if some service modifies construction sites of the database, it must publish the `SavedConstructionSiteEvent` so the `ConstructionSiteRepository` can reload its state. 

`Loaded` events are published when a repository changed its state published to its consumers.
For example, if the repository has to reload because it received a `SavedConstructionSiteEvent` it publishes as `LoadedConstructionSiteEvent` so the activities can refresh its construction site views.

Each background task also published events when they start, progress or finish. 
This allows the UI to display when something in the background is going on without having to know what.

## factories

... contain the state of the application and help to construct repositories & services.

activities resolve the repositories using the `ApplicationFactory`.

## models

... are the business models actually displayed in the view.

models are easy to understand, view-optimized models.

## repositories

... provide an abstraction of the various data sources and an easy to use API for the activities.

repositories have authority over one specific model (the `ConstructionSiteRepository` only handles `ConstructionSites`).
they expose these models to the activities.
they initiate background tasks and process its result by saving it to the database.
they convert database entities into usable models.

## services

... abstract technical details

services make the technical domain easy usable by repositories or other services.
they are easily testable.

the `SqliteService` may not be consumed by the repositories of services outside its `services.data` namespace.
its API allows for very general usage (and hence easy errors). 
hence each table/model has its own data service (like `ConstructionSiteDataService`) which should be used by other repositories and services.

## views

... contain custom UI elements
