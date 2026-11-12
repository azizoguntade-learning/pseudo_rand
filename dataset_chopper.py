import os

def chop_dataset(input_filename, output_filename, max_lines=100000):
    print(f"Opening '{input_filename}' to extract a manageable subset...")
    
    lines_written = 0
    skipped_comments = 0

    try:
        with open(input_filename, 'r') as infile, open(output_filename, 'w') as outfile:
            for line in infile:
                stripped_line = line.strip()
                
                # Skip empty lines or network repository metadata/comment lines
                if not stripped_line or stripped_line.startswith('%') or stripped_line.startswith('#'):
                    skipped_comments += 1
                    continue
                
                # Split by whitespace (common in raw edge lists: node1 node2 [weight])
                parts = stripped_line.split()
                
                if len(parts) >= 2:
                    sender = parts[0]
                    receiver = parts[1]
                    # Real datasets might have a third column for weight; if not, default to 1.0 or parse it
                    weight = parts[2] if len(parts) > 2 else "1.0"
                    
                    # Write in our standard CSV format: sender,receiver,affectance
                    outfile.write(f"{sender},{receiver},{weight}\n")
                    lines_written += 1
                    
                    # Stop once we hit our target line limit
                    if lines_written >= max_lines:
                        break
                        
        print(f"Success! Chopped dataset saved to '{output_filename}'.")
        print(f"Skipped header/comment lines: {skipped_comments}")
        print(f"Total valid links written: {lines_written}")

    except FileNotFoundError:
        print(f"Error: Could not find '{input_filename}'. Make sure the raw file is in your project directory.")

if __name__ == "__main__":
    # Change 'ant_colony_raw.txt' to whatever your downloaded dataset filename is
    input_file = "ant_colony_raw.txt"
    output_file = "affectance.txt" # Overwrite or name it something like chopped_affectance.txt
    
    # Target 100,000 lines for a smooth, fast local test run
    chop_dataset(input_file, output_file, max_lines=100000)