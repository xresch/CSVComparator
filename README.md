# CSVComparator
CSV Comparator is a tool for comparing files which contain CSV-Data. It was created for performance engineers to be able to have a easy way to compare data from two testruns and be able to automate the comparing process of various files with scripts.
There is a command line tool as well as a user interface.

In case you need to compare .xml-Files you can convert them to .csv-Files with my other project CSV Transformer.

The advantages by using CSV as input and output are the following:
- Many applications allow an export of its data to CSV or Excel(were it could be saved to CSV)
- It is possible to pre- and postprocess the data, e.g removing version numbers before comparing
- It is easy to create CSV data from logfiles and other resources by using unix tools like sed or awk
- CSV can be loaded into other applications, especially Excel, were it can be formatted by macros for analyzing.

Features
- Comparing .csv-Files column-wise
- Compare difference, difference in percentage or as strings
- Reading files with any delimiter, with or without quotes
- Making row identifiers unique before comparing
- Divide/Multiply values before comparing
- Comparing .csv-Files containing sections of data (see top level feature)
- Write result with or without quotes
- sorting the result
- GUI: saving/loading of config files
- GUI: saving of bash scripts
