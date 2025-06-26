This directory contains the raw and processed data for finding the 'pausepoints' of the audio clips. 

# Pausepoint
A pausepoint is a point at which the audio should auto-pause, to allow the user a chance to think about the answer themselves, before continuing with the answer from the student in the recording.

# Input
The raw audio data from LanguageTransfer is in `audiosource`. Each directory under `audiosource` is for a single language, and contains the unzipped files from the download. For example, `audiosource/spanish` contains the unzipped audio files from `https://downloads.languagetransfer.org/spanish/spanish.zip`.

For obvious reasons this input directory is gitignored.

# Output
The language audio directory gets processed into a single json, which contains a mapping from each audio file to all its pausepoints. That json file has the same name as the language directory, and is located in the `pausepoints` directory. So `audiosource/spanish` gets processed into `pausepoints/spanish.json`.

# Processing
First setup your API key:
- Create a `local.properties` file as a sibling of `process.py`
- Fill the file like so: `GEMINI_API_KEY=YOUR_ACTUAL_KEY_HERE`

This file is gitignored so as not to expose your personal API key

Then run the processing script like so:
- cd languagedata
- source .venv/bin/activate.fish
- python3 -m pip install google-genai
- python3 process.py "your_language"