# Release Table

## Module

Module | Version | tag | release date
------------ | ------------- | ------------- | -------------
utilities-parent | 3.0.0 |  | not yet released!
                 | 2.0.0 | v2 | 10/10/2016 used in Cohorte 1.2.0

Note: if one of the following sub-modules change the version, we should increase the major parent module version.

## Sub modules

Module | Version | changed? | Comment
------------ | ------------- | ------------- | -------------
org.cohorte.utilities | 1.0.8 | yes | version 1.0.6 used in Cohorte 1.2.0 
org.cohorte.utilities.config | 1.1.0 | no | 
org.cohorte.utilities.crypto | 1.0.1 | no
org.cohorte.utilities.installer | 1.3.3 | yes | version 1.3.3 used in OFFLine installer 1.0.11
org.cohorte.utilities.picosoc | 1.0.0 | no
org.cohorte.utilities.picosoc.webapp | 1.0.0 | no
org.cohorte.utilities.rest | 1.0.0 | no
org.cohorte.utilities.test | 1.0.1 | yes | previous version 1.0.0

Note1: "changed?" means if the sub-module is changed after the last release of the parent module.


# TODO After release:

- increment utilities-parent major version
- update parent version of all sub-module to have this new version
- for each sub-module which is changed, update its version and add a comment about where it was used before