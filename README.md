# Network DWLS Simulation Pipeline

A lightweight simulation environment 

---

## Project Structure

* **`DataGenerator.java`**: Generates a dense synthetic network (`affectance.txt`) using inverse-square law weights ($1/\text{distance}^2$).
* **`dataset_chopper.py`**: Converts raw Network Repository `.edges` files into the standardized `sender,receiver,affectance` CSV format (`affectance.txt`).
* **`DWLSSimulation.java`**: Streams `affectance.txt`, aggregates cumulative interference per receiver node, and counts how many nodes exceed the `1.0` load-shedding threshold.

---

## Quick Start Commands

### 1. Artificial Dataset Pipeline (Inverse-Square Law)
Generate synthetic stress-test data (10,000 nodes, ~100 million links) and evaluate it:
```bash
# Compile and run the synthetic data generator
javac DataGenerator.java
java DataGenerator

# Compile and run the DWLS simulation parser (with 4GB max heap)
javac DWLSSimulation.java
java -Xmx4g DWLSSimulation

### 2. Real World Data-Set Usage

# Place your downloaded .edges file (e.g., insecta-ant-colony6-day04.edges) in the root directory
# Update input_file in dataset_chopper.py if needed, then convert:
python3 dataset_chopper.py

# Compile (if not already compiled) and run the simulation parser
javac DWLSSimulation.java
java -Xmx4g DWLSSimulation