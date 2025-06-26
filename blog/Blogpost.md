Interesting things in project:

- Gemini appears to be very good at speaker diarization and timestamping
- But sometimes it makes weird mistakes following timestamping rules
- Gemini is thinking in base 60 (like minutes). It likes to skip ahead to the next magnitude when it hits '60'. For example, it will jump from timestamp 59157 to timestamp 103307 when 1 second passes
- Its important to find a timestamp format that works best for the AI. Initially I was using millisecond ints thinking that would be easiest for the AI. Turns out the AI prefers human-readable timestamps, and hallucinates less with them, probably because it has more training data in that format. 
- Sometimes it will produce non-sequential timestamps. When this happens it is always a hallucination. It happens because Gemini gets confused with the timestamp format. I added a check to spot these, and when any non-sequential timestamp is spotted the entire transcript has to be discarded, since none of it can be trusted - often the AI will start timestamping continuing on from the erronous non-sequential timestamp, meaning everything following is nonsense. The diarization is then automatically retried until we get a transcript with only sequential timestamps.

- Using Gemini to write a prompt for Gemini (making it much stricter and harsher)

- Manual checking of timestamping is always necessary because Gemini can't be fully trusted
- To do the manual checking and corrections I built a timestamp editor into the app that allows for extremely quick iteration cycles for testing