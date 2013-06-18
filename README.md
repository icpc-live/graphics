graphics
========

Build
-----
    $ ./run-client build

Test run
--------
To try out loading a contest and running the resolver, in four terminals run:

1. `$ ./quick-spider  # starts the server`

2. `$ ./quick-live  # starts the graphics with a black screen`

3. `$ ./quick-control  # starts the control program`

    Now, press "Scoreboard" to switch the graphics to scoreboard.
    Then "Resolver" to bring up the resolver control.
    Then enter:
    - Freeze time: 14400
    - Replay until: 14400
    - Replay delay: 100
    - Resolve team: 1000
    - Resolve problem: 1000
    - #Gold, Silver, Bronze, Blank: 4, 4, 4, 2
    - Winner label: Title

    Press enter after each number, so there are no yellow text fields!

    Press "Pause".

    Finally run:

4. `$ ./quick-file finals-12.txt`

    Wait.

To replay, press "Replay". To resolve, press "Resolve". To step through the final 14 teams, press "Presentation step".
