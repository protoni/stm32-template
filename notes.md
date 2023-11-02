
# MCU STM32F103R8T6
````bash
- Extended part in JLCPCB
- R8 = medium-density device
- Specs: 
  64 KB Flash
  20 KB RAM
  3 × USARTs
  3 × 16-bit timers
  2 × SPIs, 2 × I2Cs, USB,
  CAN, 1 × PWM timer
  2 × ADC
- VDDA: Analog power supply voltage
  - GZ2012D101TF Bead for filtering
    - 0.15 Ohm DCR
    - 800mA Max current
    - 100 Ohm impedance @ 100MHz
  - Or MPZ1608S101ATAH0:
    - 0.03 Ohm DCR
    - 3A Max current
    - 100 Ohm impedance @ 100MHz
- Reset pin has internal pull-up resistor
- Boot: AN2606
  - Pull low to program normally with SWD
  - Pull high using a jumper to progarm using internal ROM bootloader and UART
- User LEDs
  - PC4
  - PC5
- User Buttons
  - PB0
  - PB1
  - Add hardware debouncer with 1u cap and 10k resistor
- Status LED
  - PC3
- No connect:
  - PD2
  - PC6
  - PC7
  - PC8
  - PC13 - PC15:
    - PC13, PC14 and PC15 are supplied through the power switch and
      since the switch only sinks a limited amount of current
      (3 mA), the use of GPIOs PC13 to PC15 is restricted: only one I/O
      at a time can be used as an output, the speed has to be
      limited to 2 MHz with a maximum load of 30 pF and these I/Os must
      not be used as a current source (e.g. to drive an LED).
  - Enable internal pull-up on unconnected pins
````

````bash
# Crystal
ABLS-8.000MHZ-B4-T

# MCU OSC_IN input capacitance
5pF

# Calculate external capacitance
Crystal load capacitance      ( C_L )   = 18pF
Parasitic capacitance         ( C_P )   = ~4pF
MCU osc pin input capacitance ( C_IN )  = 5pF
External capacitance          ( C_EXT ) = ?

C_EXT = C_L + C_P + C_IN
C_EXT = 18pF + 4pF + 5pF
C_EXT = 27pF

````

````bash
# LEDs
B1911USD-20D000114U1930

# Color
Red

# Calculate current limiting resistor for LEDs
Power supply    ( V )  = 3.3V
Forward voltage ( Vf ) = 2V
Current draw    ( I )  = 5mA
Resistor value  ( R )  = ?

# Voltage across the resistor ( Vr ):
Vr = V - Vf
Vr = 3.3V - 2V
Vr = 1.3V

# Resistor value:
R = Vr/I
R = 1.3V / 0.005 A
R = 260 Ohm

# Calculate power dissipation
P = I2 * R
P = (0.005A)2 * 260 Ohm
P = 0.0065 W
P = 6.5 mW

# Resistor 
RC0603FR-13270RL = 270 Ohm / 0.1W


````

````bash
# Regulator AMS1117-3.3
- 5V -> 3.3V
- Needs 22uF tantalum capacitor for output filtering, if adjustment
  terminal is bypassed.
- Add 100n ceramic cap for output since they react faster to quick
  transient load
- Add 10uF tantalum cap for input
````

````bash
# USB
- Micro USB-B
- Leave shield floating
- Resettable fuse for VBUS
  - for example SMD0805B035TF
    - 0.10sec trip time
    - hold current is a little bit over the needed value ( 330mA ),
      but it's a lot cheaper than fuses with lower value'
- Add ESD protection diodes for USB D+/D- ( for example IP4234CZ6 )
  - TODO: Add back drive protection diode to VCC to avoid short circuit on the
    data lines when VBUS is down
- D+ line needs 1.5k Ohm pull-up resistor to 3.3V to act as a USB device, rather
  than USB host. The chip doesn't support host mode either, so there's no internal
  pull-up. One way is to pull it straight to 3.3V, but then the USB enumeration
  wont work on soft restarts ( when flashing firmware ) and the USB cable needs
  to be unplugged and plugged back in.
  - Another way is to control the pull-up resistor with a MCU pin controlled
    transistor.
    - Use for example BSS84AK pFET
    - Use PD2 pin to drive the FET
````

````bash
# Flashing
- Use 20 pin header with 2.54mm pitch for SEGGER j-link
  - VTref  -> VCC / 3.3V
  - GND    -> GND
  - TMS    -> SWDIO  (Serial Wire debug Data Input/Output)
  - TCK    -> SWDCLK (Serial Wire Clock)
  - TDO    -> SWO    (Serial Wire trace Output)
  - nRESET -> nRESET
  - leave rest of the pins unconnected
````

````bash
# TODO
- Add test points
- Change GPIO header connector, current one is expensive
````

````bash
# PCB
- 0.25mm signal trace
- 0.5
````

````bash
# Parts

# Part num.               # Package   # Value   # Info           # Type
TMCP0J106MTRF             0805        10uF      6.3V             Tantalum cap.
- C1

TL8W9226M010C             0805        22uF      10V              Tantalum cap.
- C2

VJ0805A270GXQCW1BC        0805        27pF      10V              Ceramic cap.
- C17, C18

KGM15AR70J104KM           0603        0.1uF     6.3V             Ceramic cap.
- C3, C7, C9, C11, C13, C15, C19, C21

CL10B105KQ8NNNC           0603        1uF       6.3V             Ceramic cap.
- C4, C16, C20

CL10A106MQ8NNNC           0603        10uF      6.3V             Ceramic cap.
- C5, C6, C8, C10, C12, C14

B1911USD-20D000114U1930   0603        RED       Vf 2V            LED
- D1, D2, D3, D4

RC0603FR-13270RL          0603        270Ohm    0.1W             Resistor
- R1, R5, R6, R9

SMD0805B035TF             0805        350mA     6V               Resettable Fuse
- F1

MPZ1608S101ATAH0          0603        100R      0.03 Ohm DCR/3A  Ferrite bead
- FB1, FB2

RMCF0603FT10K0            0603        10k       0.1W             Resistor
- R2, R4, R7, R8

WR06X1501FTL              0603        1k5       0.1W             Resistor
- R3

PTS636 SL43 SMTR LFS                  2Pin                       Button
- SW1, SW2, SW3    

UJ2-MIBH-G-SMT-TR                                                Micro USB-B
- J1

GPHC204-1002A001R1BF                  2x10      2.544mm female   Connector
- J4

NPPC102KFMS-RC                        2x10Pin   2.54mm pitch     Connector
- J3, J5

PHDD-SS004G1ABOGE-B005                2x2       2.544mm male     Connector
- J7

AMS1117-3.3              TO-261-4     3.3V                       Regulator
- U1

STM32F103R8T6                                                    MCU
- U2

IP4234CZ6               SOT-457                                  TVS DIODE 
- U3

ABLS-8.000MHZ-B4-T        HC-49/US    8MHz      CL 18pF          Crystal
- Y1



````