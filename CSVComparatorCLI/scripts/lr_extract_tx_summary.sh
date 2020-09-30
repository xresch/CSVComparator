#!/bin/bash
##############################################################################
# Author: Reto Scheiwiller
#
# Loadrunner extract Transaction Summary
# --------------------------------------
# 
# Syntax:     loadrunner_extract_tx_summary.sh <asc_file>
# Input:      <asc_file>:  .asc-File in saved LoadRunner Analysis Session
# Output:     Transaction Summary as CSV-Data
# 


# Check if there is one arguments present
if [ "$#" -ne 1 ]
then
	###########################################################
	# Print Help
	###########################################################
	echo "define some help..."

	exit 1
else
	#improve grep speed
	export LANG=C
	
	# Save Arguments
	SUMMARY_ASC_FILE=$1

	###########################################################
	# parse Transaction Summary Table
	# -------------------------------
	# first egrep:  filter only Rows
	# second egrep:	Remove HTTP Transaction Status Table
	# first sed:    format output to CSV-Data
	###########################################################
	cat $SUMMARY_ASC_FILE | 
	egrep "^RowNo" | 
	egrep -v "HTTP Responses|HTTP_[0-9]{3}" |
	sed -E  -e "s/RowNo.*=//" \
			-e 's/#/,/g' 
fi
