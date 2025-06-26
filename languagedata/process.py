import os
from os import listdir
from os.path import isfile, join
import sys
from google import genai
import time
import re
import json
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor, as_completed
from functools import partial

def load_api_key():
    filename="local.properties"
    key_name="GEMINI_API_KEY"

    """Loads a specific key's value from a key-value pair file."""
    try:
        with open(filename, 'r') as f:
            for line in f:
                line = line.strip()
                if line and '=' in line and not line.startswith('#'): # Ignore empty lines and comments
                    k, v = line.split('=', 1) # Split only on the first '='
                    if k == key_name:
                        if(v == ""):
                            print(f"Warning: Key '{key_name}' in '{filename}' is empty.")
                            return None
                        return v
        print(f"Warning: Key '{key_name}' not found in '{filename}'.")
        return None
    except FileNotFoundError:
        print(f"Error: The file '{filename}' was not found.")
        return None
    except Exception as e:
        print(f"An error occurred while reading the file: {e}")
        return None

# --- Function to load and process audio ---
def diarize(audio_file_path: str):
    API_KEY = load_api_key()
    client = genai.Client(api_key=API_KEY)

    model_name = "gemini-2.5-pro-preview-06-05"

    audio_file_name = Path(audio_file_path).stem

    """
    Loads an audio file and sends it to the Gemini API for processing
    (e.g., transcription).
    """
    try:
        if not os.path.exists(audio_file_path):
            print(f"Error: Audio file not found at '{audio_file_path}'")
            return None

        # Upload the file to Google's infrastructure. This is required for large files.
        audio_file = client.files.upload(file=audio_file_path)

        # Wait for the file to be ready for use by the model.
        # For larger files, this step is critical.
        # A simple polling mechanism. You might want to add a timeout or backoff.
        # This loop will continue until the file is ACTIVE or FAILED.
        start_time = time.time()
        while True:
            # Refresh the file object status
            audio_file = client.files.get(name=audio_file.name)
            state = audio_file.state.name
            if state == "FAILED":
                print(f"Error for {audio_file_name}: Audio file processing failed. State: {audio_file.state.name}")
                return None

            if state == "ACTIVE":
                break

            else:
                if time.time() - start_time > 300: # Timeout after 5 minutes
                    print(f"Error for {audio_file_name}: File processing timed out.")
                    return None
                time.sleep(5) # Wait for 5 seconds before checking again
        
        prompt = """
You are an expert audio transcription AI. Your task is to generate a **complete, accurate, and highly precise verbatim transcript** of the provided audio, including **speaker diarization** and **exact timestamps**.

---
**Absolutely Critical Output Format:**

Every single line of your output **MUST STRICTLY ADHERE** to the following precise format. Any deviation is unacceptable.

`HH:MM:SS.ms --- Speaker X --- TEXT`

---
**Format Breakdown and Strict Rules:**

1. `HH:MM:SS.ms`:

    * This represents the ABSOLUTELY EXACT START TIME of the spoken segment. It's crucial that this timestamp indicates the PRECISE MOMENT the speaker begins uttering any sound.
    * It MUST be in the format HH:MM:SS.ms:
    * HH: Two digits for hours (e.g., 00, 01, 10). Always use leading zeros if needed.
    * MM: Two digits for minutes (e.g., 00, 05, 59). Always use leading zeros if needed.
    * SS: Two digits for seconds (e.g., 00, 12, 59). Always use leading zeros if needed.
    * .ms: Three digits for milliseconds (e.g., .000, .012, .500, .999). Always use leading zeros if needed.
    * 00:00:00.000 signifies the very beginning of the audio.

    * CRITICAL TIMESTAMPS RULE: ABSOLUTE STRICT ADHERENCE REQUIRED
    * Each HH:MM:SS.ms timestamp value MUST ALWAYS BE GREATER THAN OR EQUAL TO the HH:MM:SS.ms of the immediately preceding line.
    * TIMESTAMPS MUST NEVER DECREASE. EVER. They must strictly progress forward in time or remain the same if a speaker continues their turn immediately.

2.  **`Speaker X`**:
    * Identifies the unique speaker for that segment.
    * `X` is a numerical ID (either `1` or `2`).
    * **Speaker ID Assignment**: Speaker IDs are assigned **chronologically**. The first distinct speaker identified in the audio is `Speaker 1`. If a second distinct speaker is present, they are identified as `Speaker 2`. **You will never encounter more than two distinct speakers in any audio clip.** This order **MUST be maintained consistently** throughout the entire transcript.

3.  **`TEXT`**:
    * The **verbatim text** spoken by `Speaker X` during that segment.
    * Transcribe exactly what is said, without summarization or interpretation.

---

Example of Desired Output (Observe diverse timestamps and sequential progression):

00:00:00.000 --- Speaker 1 --- Welcome to our first lesson. Today we're going to talk about how Spanish simplifies certain English endings.
00:00:05.378 --- Speaker 1 --- Think of words in English that end in 'tion'. Like 'information'.
00:00:10.112 --- Speaker 1 --- How would you try to say 'information' in Spanish? Don't worry if it's wrong, just try to apply the rule.
00:00:15.087 --- Speaker 2 --- Uh, *información*?
00:00:16.195 --- Speaker 1 --- Excellent! You got it. That 'tion' becomes 'ción'. Very good.
00:00:16.195 --- Speaker 1 --- Now try 'nation'.
00:00:20.443 --- Speaker 2 --- *Nación*.
00:00:21.050 --- Speaker 1 --- Perfect. You see the pattern?
00:00:22.491 --- Speaker 2 --- Yeah, that's pretty clear. So 'communication' would be 'comunicación'?
00:00:26.517 --- Speaker 1 --- Exactly. You're building the language already. That's the power of this method.
00:00:29.982 --- Speaker 1 --- Let's try another one. What about 'action'?
00:00:33.203 --- Speaker 2 --- *Acción*.
00:00:33.771 --- Speaker 1 --- Brilliant. Now, let's consider another common English ending.
00:00:34.999 --- Speaker 1 --- Words ending in 'ality'. Like 'reality'.
00:00:39.406 --- Speaker 2 --- Hmm, so that would be 'realidad'?
00:00:40.852 --- Speaker 1 --- Spot on! The 'ality' becomes 'alidad'.
00:00:43.729 --- Speaker 1 --- Try 'personality'.
00:00:45.011 --- Speaker 2 --- *Personalidad*.
00:00:45.639 --- Speaker 1 --- Superb. You're thinking the language.
00:00:46.997 --- Speaker 1 --- We'll continue with more patterns in our next lesson.
00:00:49.188 --- Speaker 2 --- Great, thanks.
        """

        contents = [
            audio_file,
            prompt
        ]

        print(f"Transcribing : {audio_file_name}")

        response = client.models.generate_content(
            model=model_name,
            contents=contents,
            config=genai.types.GenerateContentConfig(
                # Temperature is between 0 and 2. 1 is default. 
                # Higher temperature means more creativity, and more unpredictability.
                temperature = 1,
            ),
        )

        # Optionally, delete the uploaded file from Google's infrastructure
        # if you don't need it stored. This is good practice for privacy and cost.
        client.files.delete(name=audio_file.name)

        transcript = response.text

        transcript_file_path = f"transcript/{audio_file_name}"
        with open(transcript_file_path, "w") as output:
            output.write(transcript)

        return transcript

    except Exception as e:
        print(f"An error occurred during diarization of {audio_file_path}: {e}")
        return None

def getOrNone(list, index):
    try:
        return list[index]
    except IndexError:
        return None

# Merge lines which have the same speaker
def merge_speaker_lines(transcript_lines, file_name):
    merged_output = []
    current_speaker = None
    current_text_segments = []
    first_timestamp_for_block = None # This will store the timestamp of the very first line of the current block

    def finalise_block():
        merged_text = ". ".join(current_text_segments)
        merged_output.append(f"{first_timestamp_for_block} --- {current_speaker} --- {merged_text}")

    for line in transcript_lines:
        parts = line.strip().split(' --- ')

        timestamp_str = getOrNone(parts, 0)
        speaker_id = getOrNone(parts, 1)
        textOrNone = getOrNone(parts, 2)

        if timestamp_str is None or speaker_id is None:
            # Skip malformed lines or handle as error
            print(f"Warning for {file_name}: Skipping malformed line: {line.strip()}")
            continue

        text = "" if textOrNone is None else textOrNone
        
        if current_speaker is None:
            # First line initialization
            current_speaker = speaker_id
            current_text_segments.append(text)
            first_timestamp_for_block = timestamp_str # Capture the first timestamp
        elif speaker_id == current_speaker:
            # Same speaker, append text
            current_text_segments.append(text)
            # first_timestamp_for_block remains unchanged (it's the first one)
        else:
            # Speaker changed, finalize the previous merged segment
            finalise_block()

            # Start new segment with the new speaker
            current_speaker = speaker_id
            current_text_segments = [text]
            first_timestamp_for_block = timestamp_str # Capture the new first timestamp for this block

    # After the loop, add the last accumulated segment
    if current_speaker is not None:
        finalise_block()

    return merged_output

def hms_ms_to_milliseconds(timestamp_string: str) -> int | None:
    """
    Converts a timestamp string from HH:MM:SS.ms, MM:SS.ms, or SS.ms format
    to total milliseconds as an integer, using string splitting.

    Args:
        timestamp_string: The timestamp string (e.g., "00:00:15.087", "05:30.123", "45.678").

    Returns:
        An integer representing the total number of milliseconds if successful,
        or None if the format is invalid or contains out-of-range values.
    """
    try:
        # 1. Split by '.' to separate seconds.milliseconds
        parts = timestamp_string.split('.')
        if len(parts) != 2:
            print(f"Invalid timestamp format (missing or extra '.'): '{timestamp_string}'.")
            return None

        time_part = parts[0]
        milliseconds_str = parts[1]
        
        milliseconds = int(milliseconds_str)
        if not (0 <= milliseconds < 1000):
            print(f"Invalid milliseconds value: {milliseconds} in '{timestamp_string}'")
            return None

        # 2. Split the time part (HH:MM:SS or MM:SS or SS) by ':'
        time_components_str = time_part.split(':')

        hours = 0
        minutes = 0
        seconds = 0

        num_components = len(time_components_str)

        if num_components == 3: # HH:MM:SS format
            hours = int(time_components_str[0])
            minutes = int(time_components_str[1])
            seconds = int(time_components_str[2])
        elif num_components == 2: # MM:SS format
            minutes = int(time_components_str[0])
            seconds = int(time_components_str[1])
        elif num_components == 1: # SS format
            seconds = int(time_components_str[0])
        else:
            print(f"Invalid time component structure: '{time_part}' in '{timestamp_string}'.")
            return None

        # 3. Validate ranges for hours, minutes, seconds
        if not (0 <= hours < 100): # Assuming max 99 hours, adjust if needed
            print(f"Invalid hours value: {hours} in '{timestamp_string}'")
            return None
        if not (0 <= minutes < 60):
            print(f"Invalid minutes value: {minutes} in '{timestamp_string}'")
            return None
        if not (0 <= seconds < 60):
            print(f"Invalid seconds value: {seconds} in '{timestamp_string}'")
            return None

        # 4. Calculate total milliseconds
        total_milliseconds = (
            hours * 3600 * 1000 +    # Convert hours to milliseconds
            minutes * 60 * 1000 +    # Convert minutes to milliseconds
            seconds * 1000 +         # Convert seconds to milliseconds
            milliseconds             # Add remaining milliseconds
        )
        return total_milliseconds

    except ValueError as e:
        print(f"Invalid numeric value in timestamp: '{timestamp_string}' -> {e}")
        return None
    except Exception as e: # Catch any other unexpected errors during parsing
        print(f"An unexpected error occurred parsing '{timestamp_string}': {e}")
        return None

def extract_pausepoints(transcript: str, file_name: str) -> list[int]:
    transcript_lines = transcript.strip().split('\n') # Split the text into individual lines

    # First pass: merge any lines where the speaker is unchanged
    cleaned_transcript_lines = merge_speaker_lines(transcript_lines, file_name)

    """
    Extracts all timestamps where 'Speaker 2' is speaking from a given text output.

    Returns:
        A list of strings, where each string is a timestamp corresponding to Speaker 2.
    """
    speaker_2_timestamps = []

    for line in cleaned_transcript_lines:
        sections = line.split(' --- ')

        timestamp = getOrNone(sections, 0)
        speaker = getOrNone(sections, 1)
        if(timestamp is not None and speaker == "Speaker 2"):
            timestamp_int = hms_ms_to_milliseconds(timestamp)
            if timestamp_int is None:
                print(f"Error for {file_name}: invalid timestamp in {timestamp}")
                return None

            if len(speaker_2_timestamps) != 0:
                prev = speaker_2_timestamps[-1]
                if prev > timestamp_int:
                    print(f"Error for {file_name}: non-sequential timestamp: [..., {prev}, {timestamp_int}]. This is probably a mistake from Gemini")
                    return None
            
            speaker_2_timestamps.append(timestamp_int)
            
    return speaker_2_timestamps

def process_file_and_get_pausepoints(file_path: str):
    """
    Wrapper function to call diarize and extract_pausepoints for a single file.
    Returns a tuple of (file_name, pausepoints)
    """
    file_name = Path(file_path).name
    file_name_stem = Path(file_path).stem

    pausepoints = None
    retryCount = 0
    while pausepoints is None:
        if retryCount > 0:
            print(f"Retrying {file_name_stem}. Retry {retryCount}")
        try:
            transcript = diarize(file_path)
            if transcript:
                pausepoints_temp = extract_pausepoints(transcript, file_name_stem)
                if pausepoints_temp is not None:
                    pausepoints = pausepoints_temp
        except Exception as e:
            print(f"An error occurred processing {file_name}: {e}")

        if pausepoints is None:
            retryCount = retryCount + 1

    print(f"Processed    : {file_name_stem} : {len(pausepoints)} pausepoints")
    return file_name, pausepoints

def process_language(language: str):
    # Max concurrent tasks
    # Adjust based on your system's capabilities and API rate limits
    # Be careful not to hit Gemini's rate limits (or you will get errors)
    concurrent_tasks = 10

    audio_dir = f"audiosource/{language}"

    if not os.path.isdir(audio_dir):
        print(f"The directory '{audio_dir}' does not exist.")
        return

    files = [f for f in listdir(audio_dir) if isfile(join(audio_dir, f)) and f != ".DS_Store"]
    files.sort()

    pausepoints_data = {}
    
    # Using ThreadPoolExecutor for parallel processing
    with ThreadPoolExecutor(max_workers=concurrent_tasks) as executor:
        # Submit all tasks and store the future objects
        future_to_file = {executor.submit(process_file_and_get_pausepoints, join(audio_dir, file)): file for file in files}

        # Process results as they complete
        for future in as_completed(future_to_file):
            file_name = future_to_file[future]
            try:
                file_name_stem, pausepoints = future.result()
                pausepoints_data[file_name_stem] = pausepoints
            except Exception as exc:
                print(f'{file_name} generated an exception: {exc}')
                pausepoints_data[file_name_stem] = f"Error: {exc}"

    pausepoints_json_path = f"pausepoints/{language}.json"

    json_fields = []
    for file in pausepoints_data:
        pausepoints = pausepoints_data[file]
        json_fields.append(f"\t\"{file}\": {pausepoints}")
    json_fields.sort()
    json_fields_string = ",\n".join(json_fields)

    json_string = "{\n" + json_fields_string + "\n}"

    with open(pausepoints_json_path, "w") as output:
        output.write(json_string)

    print(f"All pausepoints written to {pausepoints_json_path}")

if __name__ == "__main__":
    def getArg(index):
        return getOrNone(sys.argv, index)

    action = getArg(1)

    if action == "language":
        language = getArg(2)
        if language is None:
            print("No language provided")
        else:
            process_language(language)

    elif action == "file":
        file = getArg(2)
        if file is None:
            print("No file provided")
        else:
            # For a single file, direct call is fine, no need for parallelization
            file_name, pausepoints = process_file_and_get_pausepoints(file)
            print(f"Pausepoints for {file_name}: {pausepoints}")

    else:
        print("No action provided. Use either 'language' or 'file'")