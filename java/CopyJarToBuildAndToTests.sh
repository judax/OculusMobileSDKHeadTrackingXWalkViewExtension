echo "Copying jar files..."
# Create the destionation folders in case they do not exist
mkdir -p ../build
if [ $? -ne 0 ];then exit $?;fi
mkdir -p ../test/libs/
if [ $? -ne 0 ];then exit $?;fi
# Copy the output jar to the final build folder and to the default test and crosswalk libs folders
cp bin/oculusmobilesdkheadtrackingxwalkviewextension.jar ../build
if [ $? -ne 0 ];then exit $?;fi
cp bin/oculusmobilesdkheadtrackingxwalkviewextension.jar ../test/libs
if [ $? -ne 0 ];then exit $?;fi
echo "Copied!"
