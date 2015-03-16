Building PiterFM
----------------

- Consult "project.properties" for appropriate version of ActionBarSherlock

- Download ActionBarSherlock from http://actionbarsherlock.com/download.html

- Unpack downloaded .zip to parent folder. Rename the extracted folder. For
  example, if project.properties contains:

    android.library.reference.1=../actionbarsherlock-4.1.0-0/library

  then rename to actionbarsherlock-4.1.0-0

- I you want to build PiterFM using Ant, generate build.xml for the app and all
  referenced projects, using:

    android update project --path .

  Optionally, change default <project name="..."> in generated build.xml

- Build from command line with:
    
    ant clean debug
  or
    ant clean release
