#!/bin/bash
##############################################################################
# Author: Reto Scheiwiller
#
# Loadrunner Scenario settings
# ------------------------------------
# This script takes a LoadRunner Scenario File (.lrs) as input, extracts some
# important values which should be checked and output it in CSV-Format.
# 
# Syntax:     loadrunner_scenario_settings.sh <lrs_file>
# Input:      <lrs_file>:  LoadRunner Scenario File (.lrs)
# Output:     CSV formatted Scenario Settings

#

# Check if there is one arguments present
if [ "$#" -ne 1 ]
then
	###########################################################
	# Print Help
	###########################################################
	echo "This script takes a LoadRunner Scenario File (.lrs) as input, extracts some"
	echo "important values which should be checked and output it in CSV-Format."
	echo ""
	echo "Syntax:     loadrunner_scenario_settings.sh <lrs_file>"
	echo "Input:      <lrs_file>:  LoadRunner Scenario File (.lrs)"
	echo "Output:     CSV formatted Scenario Settings"
	
	exit 1
else
	#Hack to improve grep speed
	export LANG=C
	
	# Save Arguments
	SCENARIO_FILE=$1
	
	###########################################################
	# parse Scheduling Settings
	# -------------------------
	# first sed:    filter by GroupScheduler Definitions
	# second sed:   merge everything on one line
	# third sed:    split Groups on new line
	# fourth sed:   parse the Values (3 different patterns)
	###########################################################
	echo "###########################################################"
	echo "# Scheduling Settings"
	echo "###########################################################"
	echo "Name,Startdelay(s),RampUpVUPerInterval,RampUpInterval(s),RunFor(s)"
	
	cat $SCENARIO_FILE | 
	sed -n -e '/<GroupScheduler>/,/<\/GroupScheduler>/{p}' |
	sed -e ':a;N;$!ba;s/\n/ /g' |
	sed -e 's/<GroupScheduler>/\n<GroupScheduler>/g' |
	sed -E  -e 's:.*<GroupName>(.*)</GroupName>(.*)<StartIntervalAfterScenarioBeginning>(.*)</StartIntervalAfterScenarioBeginning>(.*)<Count>(.*)</Count>(.*)<Interval>(.*)</Interval>(.*)<RunFor>(.*)</RunFor>.*:\1,\3,\5,\7,\9:' \
			-e 's:.*<GroupName>(.*)</GroupName>(.*)<StartAtScenarioBegining />(.*)<Count>(.*)</Count>(.*)<Interval>(.*)</Interval>(.*)<RunFor>(.*)</RunFor>.*:\1,0,\4,\6,\8:'\
			-e 's:.*<GroupName>(.*)</GroupName>(.*)<StartAtScenarioBegining />(.*)<RunAll>(.*)<RunFor>(.*)</RunFor>.*:\1,0,startallimmediately,none,\5:' |
	sort

	###########################################################
	# parse Runtime Settings
	# ----------------------
	# egrep's:      filter the Settings
	# first sed:    merge everything on one line
	# second sed:   split Groups on new line
	# third sed:    parse the Values (2 different patterns)
	###########################################################
	echo "###########################################################"
	echo "# Runtime Settings"
	echo "###########################################################"
	echo "Name,Iterations,SnapshotOnError,ContinueOnError,FailTransOnErrorMsg,LogOption,ThinkTimeOption,PacingConstantTime,PacingType"
	
	cat $SCENARIO_FILE | 
	egrep "Path=|Config=|ConfigUsp=" | 
	egrep -v "^Path=$|TestDi|FrontNet.Scenarios"|
	sed -e ':a;N;$!ba;s/\n/ /g' |
	sed -e 's/[^D]Path=/\nPath=/g' |
	sed -E  -e 's/Path=.*SnapshotOnErrorActive=([^\\]*).*ContinueOnError=([^\\]*).*FailTransOnErrorMsg=([^\\]*).*LogOptions=([^\\]*).*\[ThinkTime\]\\r\\nOptions=([^\\]*).*RunLogicPaceConstTime=([^\\]*).*RunLogicPaceType=([^\\]*).*RunLogicNumOfIterations=([^\\]*).*Name="Run".*/\1,\9,\2,\3,\4,\5,\6,\7,\8/' \
			-e 's/Path=.*SnapshotOnErrorActive=([^\\]*).*ContinueOnError=([^\\]*).*FailTransOnErrorMsg=([^\\]*).*LogOptions=([^\\]*).*\[ThinkTime\]\\r\\nOptions=([^\\]*).*RunLogicNumOfIterations=([^\\]*).*RunLogicPaceConstTime=([^\\]*).*RunLogicPaceType=([^\\]*).*Name="Run".*/\1,\7,\2,\3,\4,\5,\6,\8,\9/' |
	sort

fi
