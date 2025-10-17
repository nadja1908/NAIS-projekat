#!/bin/bash

# Wait for Cassandra to be ready
echo "Waiting for Cassandra to start..."
sleep 30

# Run the init script
echo "Running Cassandra initialization script..."
cqlsh -f /cassandra-init.cql

echo "Cassandra initialization completed!"