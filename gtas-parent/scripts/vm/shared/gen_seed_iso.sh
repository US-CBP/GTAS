#!/bin/bash

CURRENT_DIR=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $CURRENT_DIR

###
## 
###
gen_seed_iso(){
    mkisofs -output seed.iso -volid cidata -joliet -rock meta-data user-data
}


gen_seed_iso