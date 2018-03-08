#!/bin/bash
cat countries.sql carriers.sql airports.sql views.sql gtas_data.sql | mysql -u root -p gtas
