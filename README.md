fabric-loader (AlexIIL's fork)
==============================

The loader for mods under Fabric. It provides mod loading facilities and useful abstractions for other mods to use.

## License

Licensed under the Apache License 2.0.

## This fork

This contains several branches which might be of interest:

### PR's

#### (open) [alexiil-package-loading](https://github.com/AlexIIL/fabric-loader/tree/alexiil-package-loading)
PR [#196](https://github.com/FabricMC/fabric-loader/pull/196).

This primarily implements the "Sealed" attribute for packages.

#### (merged) [alexiil-fixed-gui-error-log](https://github.com/AlexIIL/fabric-loader/tree/alexiil-fixed-gui-error-log)
PR [#186](https://github.com/FabricMC/fabric-loader/pull/186)

This fixed a small bug where the error gui didn't log the errors.

### Misc

#### The loader GUI
These branches all relate to the GUI for fabric loader (PR [#154](https://github.com/FabricMC/fabric-loader/pull/154))

There are several branches:
* [alexiil-full-gui](https://github.com/AlexIIL/fabric-loader/tree/alexiil-full-gui) The merged GUI code.
* [alexiil-additional-gui](https://github.com/AlexIIL/fabric-loader/tree/alexiil-additional-gui) The original GUI code, with more features.

### Experimental fabric.mod.json schemaVersion 2 branches

Work in progress: I haven't got these to a decent enough state to want to talk about them quite yet. (In particular I need to know if these are even feasible)

#### [dev/0.7-alexiil-json-apis](https://github.com/AlexIIL/fabric-loader/tree/dev/0.7-alexiil-json-apis)
A way for one mod to declare a class or interface as an API, and for a (single) other mod to implement that API.

Client code can use `FabricLoader.getApiInstance(Class)` to get the current implementation.
