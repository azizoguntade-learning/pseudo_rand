import os

def chop_dataset(input_filename, output_filename, max_lines=100000):
    print(f"Opening '{input_filename}' to convert real network data...")
    
    lines_written = 0
    skipped_comments = 0

    try:
        with open(input_filename, 'r') as infile, open(output_filename, 'w') as outfile:
            for line in infile:
                stripped_line = line.strip()
                
                # Skip empty lines or network repository comment lines
                if not stripped_line or stripped_line.startswith('%') or stripped_line.startswith('#'):
                    skipped_comments += 1
                    continue
                
                # Split by whitespace (node pairs in raw edge lists)
                parts = stripped_line.split()
                
                if len(parts) >= 2:
                    sender = parts[0]
                    receiver = parts[1]
                    # If the edge list includes a weight/probability column, use it; otherwise default to 1.0
                    weight = parts[2] if len(parts) > 2 else "1.0"
                    
                    # Write in standard CSV format: sender,receiver,affectance
                    outfile.write(f"{sender},{receiver},{weight}\n")
                    lines_written += 1
                    
                    if lines_written >= max_lines:
                        break
                        
        print(f"Success! Converted dataset saved to '{output_filename}'.")
        print(f"Skipped comment lines: {skipped_comments}")
        print(f"Total links processed: {lines_written}")

    except FileNotFoundError:
        print(f"Error: Could not find '{input_filename}'. Ensure it's in the same folder.")

if __name__ == "__main__":
    # Point directly to file
    input_file = "insecta-ant-colony6-day04.edges"
    output_file = "affectance.txt" # This line overwrites affectance.txt with the real ant colony data
    
    chop_dataset(input_file, output_file)