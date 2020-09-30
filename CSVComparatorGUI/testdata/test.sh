#!/bin/bash
##################################################################################
# This script was generated used CSVComparatorGUI:
#		http://sourceforge.net/projects/csvcomparator/
# 
# Compare CSV Files
# ------------------------------------
# This script will compare the CSV-Files set in the variables.
# This script takes no arguments, please set the variables in the script.
#
##################################################################################

# Check if there are no arguments present
if [ "$#" -ne 0 ]
then
    ###########################################################
    # Print Help
    ###########################################################
    echo "This script takes no arguments, set the variables in the script."

    exit 1
else

    ###########################################################
    # Set Configuration
    ###########################################################
    
    ##### Java Environment Configration #####
    JAVA='/cygdrive/c/Program Files (x86)/Java/jre7/bin/java'
    CSVCOMPARE_JAR='C:\Program Files\CSVComparator\CSVComparatorCLI.jar'

    #older File
    OLDER_FILE='D:\Users\rscheiwi\MyFiles\Programmierung\_EWS\Workspace_Default\CSVComparatorGUI\testdata\older_topLevel.csv'
    OLDER_LABEL='old'
    OLDER_DELIMITER=';'
        
    #newer File
    YOUNGER_FILE='D:\Users\rscheiwi\MyFiles\Programmierung\_EWS\Workspace_Default\CSVComparatorGUI\testdata\younger_topLevel.csv'
    YOUNGER_LABEL='young'
    YOUNGER_DELIMITER=';'
    
    ###########################################################
    # Compare Files
    ###########################################################
    
    "${JAVA}" -jar "${CSVCOMPARE_JAR}"\
					"-older.file=${OLDER_FILE}"\
					"-older.delimiter=${OLDER_DELIMITER}"\
					"-older.label=${OLDER_LABEL}"\
					"-younger.file=${YOUNGER_FILE}"\
					"-younger.delimiter=${YOUNGER_DELIMITER}"\
					"-younger.label=${YOUNGER_LABEL}"\
					"-column.identifier=Description,Description"\
					"-result.file=D:\Users\rscheiwi\MyFiles\Programmierung\_EWS\Workspace_Default\CSVComparatorGUI\testdata\_result.csv"\
					"-result.delimiter=;"\
					"-result.sort=true"\
					"-column.identifier.makeunique=true"\
					"-result.comparediff=false"\
					"-result.comparediff%=true"\
					"-result.comparestring=false"\
					"-config.loglevel.console=OFF"
fi
