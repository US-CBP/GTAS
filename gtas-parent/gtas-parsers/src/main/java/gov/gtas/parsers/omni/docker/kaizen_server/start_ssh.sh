#!/bin/bash

__create_ssh_user() {
# Create a user to SSH into as.
SSH_USERNAME=kaizen
SSH_USERPASS=modelo
useradd $SSH_USERNAME
#useradd user
#echo -e "$SSH_USERPASS\n$SSH_USERPASS" | (passwd --stdin user)
echo -e "$SSH_USERPASS\n$SSH_USERPASS" | (passwd --stdin $SSH_USERNAME)
echo ssh user password: $SSH_USERPASS
}

# Call all functions
__create_ssh_user