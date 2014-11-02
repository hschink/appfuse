Experiment
==========

This is a guide for setting up AppFuse for an experiment regarding Java to
(relational) database interaction.

Prerequisites
-------------

1. Cloned repository (the target directory is referenced as ``APPFUSE_DIR``)
```> git clone https://github.com/hschink/appfuse.git```
2. Eclipse (tested with Luna release 4.4.0)
3. Eclipse Plug-In m2e

Installation
------------

1. In Eclipse go to _File > Import_ and select _Maven > Existing Maven Projects_
2. Select ``APPFUSE_DIR`` as _Root Directory_ and select **only** ``data/pom.xml``, ``common/pom.xml``, and ``hibernate/pom.xml``
3. Set [the path](https://github.com/hschink/appfuse/blob/experiment/pom.xml#L594-L596) to a current instance of the appfuse databse. You find [an instance in the repository](https://github.com/hschink/appfuse/blob/experiment/appfuse_db.mv.db).
4. Check installation by running the tests: On the project ``appfuse-data`` go to _Run As > Maven test_
