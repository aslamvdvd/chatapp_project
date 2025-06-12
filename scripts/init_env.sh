#!/bin/bash
# This script generates a .env file from the .env.example template.
#
# Usage: ./scripts/init_env.sh

set -e

if [ -f .env ]; then
  echo ".env file already exists. Please remove it first if you want to regenerate it."
  exit 1
fi

if [ ! -f .env.example ]; then
  echo "Error: .env.example not found. Cannot create .env file."
  exit 1
fi

cp .env.example .env

echo ".env file created successfully from .env.example."
echo "Please review and update the variables in the .env file."
