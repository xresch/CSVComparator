#!/bin/bash
##############################################################################
# Author: Reto Scheiwiller
#
# Loadrunner Scenario Script List
# ------------------------------------
# This script takes a LoadRunner Scenario File (.lrs) as input, extracts the
# Scriptnames of the included scripts and outputs it as a list.
# 
# Syntax:     loadrunner_scenario_scriptlist.sh <lrs_file>
# Input:      <lrs_file>:  LoadRunner Scenario File (.lrs)
# Output:     List of Scripts included in the Scenario
# Example:    loadrunner_scenario_scriptlist.sh "/cygdrive/c/Scenario.lrs"

# Check if there is one arguments present
if [ "$#" -ne 1 ]
then
	###########################################################
	# Print Help
	###########################################################
	echo "This script takes a LoadRunner Scenario File (.lrs) as input, extracts the"
	echo "Scriptnames of the included scripts and outputs it as a list."
	echo ""
	echo "Syntax:     loadrunner_scenario_scriptlist.sh <lrs_file>"
	echo "Input:      <lrs_file>:  LoadRunner Scenario File (.lrs)"
	echo "Output:     List of Scripts included in the Scenario"
	
	exit 1
else

	#improve grep speed
	export LANG=C
	
	# Save Arguments
	SCENARIO_FILE=$1

	###########################################################
	# parse Runtime Settings
	# ----------------------
	# egrep's:      filter the Settings
	# first sed:    merge everything on one line
	# second sed:   split Groups on new line
	# third sed:    parse for ScriptNames
	###########################################################
	cat $SCENARIO_FILE | 
	egrep "Path=|Config=|ConfigUsp=" | 
	egrep -v "^Path=$|TestDi|FrontNet.Scenarios"|
	sed -e ':a;N;$!ba;s/\n/ /g' |
	sed -e 's/[^D]Path=/\nPath=/g' |
	sed -E  -e 's/Path=C:\\PATH\\TO\\SCRIPT\\FOLDER\\([^\\]*).*/\1/' |
	sort
fi
