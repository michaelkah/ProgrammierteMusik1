import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

/*
    My first collection of "Programmierte Musik",
    available both as Java source code as well as midi files.

    Piece names are random 3 digit numbers.

    Usage: run the main method in this class, this will (re-)generate the midi files.
 */
class ProgrammierteMusik1 {

    /*
     * Note Names
     */

    static final int C_2 = 0;
    static final int CIS_2 = 1;
    static final int D_2 = 2;
    static final int DIS_2 = 3;
    static final int E_2 = 4;
    static final int F_2 = 5;
    static final int FIS_2 = 6;
    static final int G_2 = 7;
    static final int GIS_2 = 8;
    static final int A_2 = 9;
    static final int AIS_2 = 10;
    static final int B_2 = 11;

    static final int C_1 = 12;
    static final int CIS_1 = 13;
    static final int D_1 = 14;
    static final int DIS_1 = 15;
    static final int E_1 = 16;
    static final int F_1 = 17;
    static final int FIS_1 = 18;
    static final int G_1 = 19;
    static final int GIS_1 = 20;
    static final int A_1 = 21;
    static final int AIS_1 = 22;
    static final int B_1 = 23;

    static final int C0 = 24;
    static final int CIS0 = 25;
    static final int D0 = 26;
    static final int DIS0 = 27;
    static final int E0 = 28;
    static final int F0 = 29;
    static final int FIS0 = 30;
    static final int G0 = 31;
    static final int GIS0 = 32;
    static final int A0 = 33;
    static final int AIS0 = 34;
    static final int B0 = 35;

    static final int C1 = 36;
    static final int CIS1 = 37;
    static final int D1 = 38;
    static final int DIS1 = 39;
    static final int E1 = 40;
    static final int F1 = 41;
    static final int FIS1 = 42;
    static final int G1 = 43;
    static final int GIS1 = 44;
    static final int A1 = 45;
    static final int AIS1 = 46;
    static final int B1 = 47;

    static final int C2 = 48;
    static final int CIS2 = 49;
    static final int D2 = 50;
    static final int DIS2 = 51;
    static final int E2 = 52;
    static final int F2 = 53;
    static final int FIS2 = 54;
    static final int G2 = 55;
    static final int GIS2 = 56;
    static final int A2 = 57;
    static final int AIS2 = 58;
    static final int B2 = 59;

    static final int C3 = 60;
    static final int CIS3 = 61;
    static final int D3 = 62;
    static final int DIS3 = 63;
    static final int E3 = 64;
    static final int F3 = 65;
    static final int FIS3 = 66;
    static final int G3 = 67;
    static final int GIS3 = 68;
    static final int A3 = 69;
    static final int AIS3 = 70;
    static final int B3 = 71;

    static final int C4 = 72;
    static final int CIS4 = 73;
    static final int D4 = 74;
    static final int DIS4 = 75;
    static final int E4 = 76;
    static final int F4 = 77;
    static final int FIS4 = 78;
    static final int G4 = 79;
    static final int GIS4 = 80;
    static final int A4 = 81;
    static final int AIS4 = 82;
    static final int B4 = 83;

    static final int C5 = 84;
    static final int CIS5 = 85;
    static final int D5 = 86;
    static final int DIS5 = 87;
    static final int E5 = 88;
    static final int F5 = 89;
    static final int FIS5 = 90;
    static final int G5 = 91;
    static final int GIS5 = 92;
    static final int A5 = 93;
    static final int AIS5 = 94;
    static final int B5 = 95;

    static final int C6 = 96;
    static final int CIS6 = 97;
    static final int D6 = 98;
    static final int DIS6 = 99;
    static final int E6 = 100;
    static final int F6 = 101;
    static final int FIS6 = 102;
    static final int G6 = 103;
    static final int GIS6 = 104;
    static final int A6 = 105;
    static final int AIS6 = 106;
    static final int B6 = 107;

    static final int C7 = 108;
    static final int CIS7 = 109;
    static final int D7 = 110;
    static final int DIS7 = 111;
    static final int E7 = 112;
    static final int F7 = 113;
    static final int FIS7 = 114;
    static final int G7 = 115;
    static final int GIS7 = 116;
    static final int A7 = 117;
    static final int AIS7 = 118;
    static final int B7 = 119;

    static final int C8 = 120;
    static final int CIS8 = 121;
    static final int D8 = 122;
    static final int DIS8 = 123;
    static final int E8 = 124;
    static final int F8 = 125;
    static final int FIS8 = 126;
    static final int G8 = 127;

    /*
     * Miscellaneous Constants And Default Values
     */

    static final String PIECE_METHOD_PREFIX = "piece";
    static final String FILENAME_SUFFIX = ".midi";

    static final int DEFAULT_QUARTER_TICKS = 1024;
    static final int DEFAULT_CHANNEL = 0;
    static final int DEFAULT_ON_VELOCITY = 64;
    static final int DEFAULT_OFF_VELOCITY = 0;

    static final int META_END_OF_TRACK_TYPE = 0x2F;
    static final int META_TEXT_EVENT_TYPE = 1;

    /*
     * Current Sequence/Track
     */

    Sequence sequence;
    Track track;

    /*
     * Note Length Methods
     */

    static long naturalize(Random random, long averageValue, long maximumDeviation) {
        return Math.round(averageValue + maximumDeviation * (random.nextDouble() * 2.0 - 1.0));
    }

    long dotted(long ticks) {
        return ticks * 3 / 2;
    }

    long longaTicks() {
        return breveTicks() * 2;
    }

    long breveTicks() {
        return wholeTicks() * 2;
    }

    long wholeTicks() {
        return halfTicks() * 2;
    }

    long halfTicks() {
        return quarterTicks() * 2;
    }

    long quarterTicks() {
        return sequence.getResolution();
    }

    long eighthTicks() {
        return quarterTicks() / 2;
    }

    long sixteenthTicks() {
        return eighthTicks() / 2;
    }

    long thirtySecondTicks() {
        return sixteenthTicks() / 2;
    }

    long sixtyFourthTicks() {
        return thirtySecondTicks() / 2;
    }

    long hundredTwentyEighthTicks() {
        return sixtyFourthTicks() / 2;
    }

    long twoHundredFiftySixthTicks() {
        return hundredTwentyEighthTicks() / 2;
    }

    long fiveHundredTwelfthTicks() {
        return twoHundredFiftySixthTicks() / 2;
    }

    long oneThousandTwentyFourthTicks() {
        return fiveHundredTwelfthTicks() / 2;
    }

    /*
     * Main Method And Piece Creation Methods
     */

    public static void main(String[] args) throws Exception {
        ProgrammierteMusik1 _this = new ProgrammierteMusik1();
        Method[] methods = _this.getClass().getDeclaredMethods();

        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith(PIECE_METHOD_PREFIX)) {
                String pieceName = methodName.substring(PIECE_METHOD_PREFIX.length());

                System.out.println("Processing " + pieceName + " ...");

                _this.newPiece(DEFAULT_QUARTER_TICKS);
                method.invoke(_this, (Object[]) null);
                _this.track.add(new MidiEvent(new EndOfTrackMetaMessage(), _this.track.ticks() + _this.breveTicks()));
                _this.save(pieceName);
            }
        }

        System.out.println("Finished.");
    }

    static class EndOfTrackMetaMessage extends MetaMessage {
        EndOfTrackMetaMessage() {
            super(new byte[3]);
            data[0] = (byte) META;
            data[1] = META_END_OF_TRACK_TYPE;
            data[2] = 0;
        }
    }

    void newPiece(int pQuarterTicks) throws InvalidMidiDataException {
        sequence = new Sequence(Sequence.PPQ, pQuarterTicks);
        track = sequence.createTrack();
    }

    void note(int pKey,
              long pStartTick,
              long pTickDuration)
            throws InvalidMidiDataException {
        addEvent(NOTE_ON, pKey, DEFAULT_ON_VELOCITY, pStartTick);
        addEvent(NOTE_OFF, pKey, DEFAULT_OFF_VELOCITY, pStartTick + pTickDuration);
    }

    void softNote(int pKey,
                  long pStartTick,
                  long pTickDuration)
            throws InvalidMidiDataException {
        addEvent(NOTE_ON, pKey, DEFAULT_ON_VELOCITY / 2, pStartTick);
        addEvent(NOTE_OFF, pKey, DEFAULT_OFF_VELOCITY, pStartTick + pTickDuration);
    }

    void addEvent(int pCommand,
                  int pData1,
                  int pData2,
                  long pTick)
            throws InvalidMidiDataException {
        ShortMessage shortMessage = new ShortMessage();
        shortMessage.setMessage(pCommand, DEFAULT_CHANNEL, pData1, pData2);

        MidiEvent midiEvent = new MidiEvent(shortMessage, pTick);

        track.add(midiEvent);
    }

    void addMetaTextEvent(String text) throws InvalidMidiDataException {
        byte[] data = text.getBytes(UTF_8);
        track.add(new MidiEvent(new MetaMessage(META_TEXT_EVENT_TYPE, data, data.length), 0));
    }

    void save(String pFilename) throws IOException {
        MidiSystem.write(sequence, 0, new File("musik/" + pFilename + FILENAME_SUFFIX));
    }

    void play() throws MidiUnavailableException, InvalidMidiDataException {
        Sequencer sequencer;
        Synthesizer synthesizer;

        sequencer = MidiSystem.getSequencer();
        sequencer.open();
        sequencer.setSequence(sequence);

        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();

        sequencer.addMetaEventListener(event -> {
            if (event.getType() == 47) {
                sequencer.close();
                synthesizer.close();

                System.exit(0);
            }
        });

        Receiver receiver = synthesizer.getReceiver();
        Transmitter transmitter = sequencer.getTransmitter();
        transmitter.setReceiver(receiver);

        sequencer.start();
    }

    /*
     * Pieces
     */

    void piece217() throws Exception {
        addMetaTextEvent("March 2007, reworked September 2025. Inspired by Marble Madness.");

        long durationHigh = dotted(sixteenthTicks());
        long durationLow = breveTicks();

        long endTick = durationLow * 9;

        for (long tick = 0; tick < endTick; tick++) {
            if (tick % durationHigh == 0) {
                softNote((tick / durationHigh) % 2 == 0 ? D4 : A3, tick + 1, durationHigh);
            }

            if (tick % durationLow == 0) {
                if ((tick / durationLow) % 3 > 0) {
                    note((tick / durationLow) % 3 == 1 ? D2 : A1, tick + 1, durationLow);
                }
            }
        }
    }

    void piece945() throws Exception {
        addMetaTextEvent("March 2007, reworked September 2025. Conceived during a trip to the Deutsches Museum.");

        int initialKey = A3;
        int endKey = F4;
        long duration = dotted(quarterTicks());
        long tick = 1;

        for (int currentEndKey = initialKey; currentEndKey <= endKey; currentEndKey++) {
            for (int n = 0; n < 2 * (endKey - currentEndKey); n++) {
                note(initialKey, tick, duration);
                tick += duration;
            }

            for (int key = initialKey; key < currentEndKey; key++) {
                note(key, tick, duration);
                tick += duration;
            }

            for (int key = currentEndKey; key >= initialKey; key--) {
                note(key, tick, duration);
                tick += duration;
            }
        }
    }

    void piece346() throws Exception {
        addMetaTextEvent("March 2007, reworked September 2025. Conceived during a trip to the Deutsches Museum.");

        int initialKey = A3;
        int endKey = A4;
        long duration = breveTicks();
        long tick = 1;

        for (int currentEndKey = initialKey; currentEndKey <= endKey; currentEndKey++) {
            long currentDuration = duration / (2L * (currentEndKey - initialKey) + 1);

            for (int key = initialKey; key < currentEndKey; key++) {
                note(key, tick, currentDuration);
                tick += currentDuration;
            }

            for (int key = currentEndKey; key >= initialKey; key--) {
                note(key, tick, currentDuration);
                tick += currentDuration;
            }
        }
    }

    void piece644() throws Exception {
        addMetaTextEvent("September 2006, reworked September 2025. Random notes.");

        randomNotes(1, 2, 100, F3, E4, new long[]
                {
                        dotted(halfTicks()), halfTicks(),
                        dotted(quarterTicks()), quarterTicks(),
                        dotted(eighthTicks()), eighthTicks()
                });
    }

    void piece318() throws Exception {
        addMetaTextEvent("September 2006, reworked September 2025. Random notes.");

        randomNotes(1, 2, 200, F2, E5, new long[]
                {
                        quarterTicks(),
                        eighthTicks(),
                        sixteenthTicks()
                });
    }

    void piece433() throws Exception {
        addMetaTextEvent("September 2006, reworked September 2025. Random notes.");

        randomNotes(1, 2, 1000, A4, G5, new long[]
                {
                        dotted(halfTicks()), halfTicks(),
                        dotted(quarterTicks()), quarterTicks(),
                        dotted(eighthTicks()), eighthTicks()
                });

        randomNotes(2, 1, 1000, A1, G2, new long[]
                {
                        dotted(halfTicks()), halfTicks(),
                        dotted(quarterTicks()), quarterTicks(),
                        dotted(eighthTicks()), eighthTicks()
                });
    }

    void randomNotes(int keySeed, int durationSeed, int notes, int lowestNote, int highestNote, long[] durations) throws Exception {
        Random keyRandom = new Random(keySeed);
        Random durationRandom = new Random(durationSeed);

        long tick = 1;

        for (int n = 0; n < notes; n++) {
            int key = lowestNote + keyRandom.nextInt(highestNote - lowestNote);
            long duration = durations[durationRandom.nextInt(durations.length)];

            note(key, tick += duration, duration);
        }
    }

    void piece745() throws Exception {
        addMetaTextEvent("August 2006, reworked September 2025. 314 Digits of Pi.");

        int[] piDigits = {
                3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5, 8, 9, 7, 9, 3, 2, 3, 8, 4, 6, 2, 6, 4, 3, 3, 8, 3, 2, 7,
                9, 5, 0, 2, 8, 8, 4, 1, 9, 7, 1, 6, 9, 3, 9, 9, 3, 7, 5, 1, 0, 5, 8, 2, 0, 9, 7, 4, 9, 4,
                4, 5, 9, 2, 3, 0, 7, 8, 1, 6, 4, 0, 6, 2, 8, 6, 2, 0, 8, 9, 9, 8, 6, 2, 8, 0, 3, 4, 8, 2,
                5, 3, 4, 2, 1, 1, 7, 0, 6, 7, 9, 8, 2, 1, 4, 8, 0, 8, 6, 5, 1, 3, 2, 8, 2, 3, 0, 6, 6, 4,
                7, 0, 9, 3, 8, 4, 4, 6, 0, 9, 5, 5, 0, 5, 8, 2, 2, 3, 1, 7, 2, 5, 3, 5, 9, 4, 0, 8, 1, 2,
                8, 4, 8, 1, 1, 1, 7, 4, 5, 0, 2, 8, 4, 1, 0, 2, 7, 0, 1, 9, 3, 8, 5, 2, 1, 1, 0, 5, 5, 5,
                9, 6, 4, 4, 6, 2, 2, 9, 4, 8, 9, 5, 4, 9, 3, 0, 3, 8, 1, 9, 6, 4, 4, 2, 8, 8, 1, 0, 9, 7,
                5, 6, 6, 5, 9, 3, 3, 4, 4, 6, 1, 2, 8, 4, 7, 5, 6, 4, 8, 2, 3, 3, 7, 8, 6, 7, 8, 3, 1, 6,
                5, 2, 7, 1, 2, 0, 1, 9, 0, 9, 1, 4, 5, 6, 4, 8, 5, 6, 6, 9, 2, 3, 4, 6, 0, 3, 4, 8, 6, 1,
                0, 4, 5, 4, 3, 2, 6, 6, 4, 8, 2, 1, 3, 3, 9, 3, 6, 0, 7, 2, 6, 0, 2, 4, 9, 1, 4, 1, 2, 7,
                3, 7, 2, 4, 5, 8, 7, 0, 0, 6, 6, 0, 6, 3
        };

        long tick = 1;

        for (int piDigit : piDigits) {
            int key = A4 + piDigit;
            long duration = quarterTicks();

            note(key, tick += duration, duration);
        }
    }

    void piece671() throws Exception {
        addMetaTextEvent("August 2006, reworked September 2025. Inspired by a wall of sound.");

        long overallDuration = 8 * longaTicks();

        for (int n = 1; n <= overallDuration; n++) {
            if (n % longaTicks() == 0) {
                note(C3, n, longaTicks());
            }

            if (n % breveTicks() == 0) {
                note(E3, n, breveTicks());
            }

            if (n % wholeTicks() == 0) {
                note(G3, n, wholeTicks());
            }

            if (n % halfTicks() == 0) {
                note(B3, n, halfTicks());
            }

            if (n % quarterTicks() == 0) {
                note(D3, n, quarterTicks());
            }

            if (n % eighthTicks() == 0) {
                note(F3, n, eighthTicks());
            }
        }
    }

    void piece447() throws Exception {
        addMetaTextEvent("September 2025. Inspired by a wall of sound.");

        long overallDuration = 8 * longaTicks();

        for (int n = 1; n <= overallDuration; n++) {
            if (n % longaTicks() == 0) {
                note(F3, n, longaTicks());
            }

            if (n % breveTicks() == 0) {
                note(D3, n, breveTicks());
            }

            if (n % wholeTicks() == 0) {
                note(B3, n, wholeTicks());
            }

            if (n % halfTicks() == 0) {
                note(G3, n, halfTicks());
            }

            if (n % quarterTicks() == 0) {
                note(E3, n, quarterTicks());
            }

            if (n % eighthTicks() == 0) {
                note(C3, n, eighthTicks());
            }
        }
    }

    void piece598() throws Exception {
        addMetaTextEvent("August 2006, reworked September 2025. Eighth notes.");

        int notes = 192;

        for (int n = 0; n < notes; n++) {
            note(F3, 1 + (long) n * eighthTicks(), eighthTicks());
        }
    }

    void piece936() throws Exception {
        addMetaTextEvent("August 2006, reworked September 2025. Longa notes.");

        int notes = 12;

        for (int n = 0; n < notes; n++) {
            note(C3, 1 + (long) n * longaTicks(), longaTicks());
        }
    }

    void piece879() throws Exception {
        addMetaTextEvent("August 2006, reworked September 2025. Inspired by a pendulum wave.");

        long step1 = quarterTicks();
        long step2 = quarterTicks() + twoHundredFiftySixthTicks();
        long overallDuration = (leastCommonMultipleEuclid(step1, step2) * 2) + 1;

        for (int n = 0; n < overallDuration; n++) {
            if (n % step1 == 0) {
                note(A4, n + 1, eighthTicks());
            }

            if (n % step2 == 0) {
                note(A4, n + 1, eighthTicks());
            }
        }
    }

    long leastCommonMultipleEuclid(long a, long b) {
        return a * b / greatestCommonDivisorEuclid(a, b);
    }

    long greatestCommonDivisorEuclid(long a, long b) {
        if (b == 0) {
            return a;
        } else {
            return greatestCommonDivisorEuclid(b, a % b);
        }
    }

    void piece253() throws Exception {
        addMetaTextEvent("April 2006, reworked September 2025. Crossing sequences.");

        int start = C0;
        int end = C5;

        long averageDuration = dotted(halfTicks());
        long maximumDeviation = dotted(sixtyFourthTicks());

        long tick = 1;

        Random random = new Random(0);

        for (int n = start; n <= end; n++) {
            long duration = naturalize(random, averageDuration, maximumDeviation);

            note(n, tick, duration);
            note(start + end - n, tick, duration);

            tick += duration;
        }
    }

    void piece744() throws Exception {
        addMetaTextEvent("April 2006, reworked September 2025. Passages.");

        passages(E1, FIS5, new long[]{wholeTicks(),
                dotted(halfTicks()), halfTicks(),
                dotted(quarterTicks()), quarterTicks(),
                dotted(eighthTicks()), eighthTicks(),
                dotted(sixteenthTicks()), sixteenthTicks(),
                dotted(thirtySecondTicks()), thirtySecondTicks(),
                dotted(sixtyFourthTicks()), sixtyFourthTicks(),
                dotted(hundredTwentyEighthTicks()), hundredTwentyEighthTicks(),
                dotted(twoHundredFiftySixthTicks()), twoHundredFiftySixthTicks()});
    }

    void piece972() throws Exception {
        newPiece(128);
        addMetaTextEvent("April 2006, reworked September 2025. Passages.");

        passages(FIS0, FIS5, new long[]{1019, 947, 877, 811, 739, 661, 607, 547, 467, 419, 353, 283, 233, 179, 127, 73, 31, 2});
    }

    void passages(int startKey, int endKey, long[] durations) throws Exception {
        int keyCount = endKey - startKey;

        for (long duration : durations) {
            long tick = 1 + keyCount * durations[0];

            for (int n = endKey; n >= startKey; n--) {
                note(n, tick, duration);

                tick -= duration;
            }
        }
    }

    void piece475() throws Exception {
        addMetaTextEvent("April 2006, reworked September 2025. Sweeping velocity increase.");

        Random random = new Random(1);
        int[] melody = new int[8];
        for (int n = 0; n < melody.length; n++) {
            melody[n] = A4 + random.nextInt(GIS5 - A4 + 1);
        }

        sweepingVelocity(melody, 8, wholeTicks(), sixtyFourthTicks());
    }

    void sweepingVelocity(int[] melody,
                          int loops,
                          long startDuration,
                          long stopDuration)
            throws Exception {
        int steps = melody.length * loops;
        long tick = 1;

        for (int n = 0; n < steps; n++) {
            int key = melody[n % melody.length];
            long duration = startDuration + (((stopDuration - startDuration) * n) / steps);
            note(key, tick, duration);
            tick += duration;
        }
    }

    void piece808() throws Exception {
        addMetaTextEvent("September 2025. Fixed velocity increase.");

        Random random = new Random(1);
        int[] melody = new int[8];
        for (int n = 0; n < melody.length; n++) {
            melody[n] = A4 + random.nextInt(GIS5 - A4 + 1);
        }

        long tick = 1;
        long duration = wholeTicks();

        for (int m = 0; m < 10; m++) {
            for (int key : melody) {
                note(key, tick, duration);
                tick += duration;
            }
            duration = duration * 2 / 3;
        }
    }
}
