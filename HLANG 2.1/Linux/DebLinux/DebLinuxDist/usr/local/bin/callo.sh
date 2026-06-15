#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Usage: callo <filename>"
    exit 1
fi

touch "$1.hlang"