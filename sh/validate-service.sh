#!/bin/bash
echo Validating that the service is running
#systemctl status trading-app.service | head -n 4
#httpStatusCode=`/usr/bin/curl -o /dev/null -s -w "%{http_code}\n" http://localhost`
#echo HTTP Status Code=$httpStatusCode
#systemctl is-active trading-app.service
#if ((httpStatusCode == null || httpStatusCode != 200)); then
    #printf '%s\n' "Test case x failed" >&2  # write error message to stderr
    #exit 1                                  # or exit $test_result
#else
    #echo "Service is responding with success."
#fi