#!/bin/bash
_DEBUG_ARGS=""
if [ -n "$DEBUG" ]; then
  _DEBUG_ARGS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
fi
java -jar -Xms1G -Xmx1G -XX:MaxMetaspaceSize=384M $_DEBUG_ARGS build/libs/shell.jar "$@"
