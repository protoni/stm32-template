
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
````

````bash
# Parts

# Part num.         # Package   # Value   # Info           # Type
TMCP0J106MTRF       0805        10uF      6.3V             Tantalum cap.
- C1

TL8W9226M010C       0805        22uF      10V              Tantalum cap.
- C2

VJ0805A270GXQCW1BC  0805        27pF      10V              Ceramic cap.
- C17, C18

KGM15AR70J104KM     0603        0.1uF     6.3V             Ceramic cap.
- C3, C7, C9, C11, C13, C15, C19, C21

CL10B105KQ8NNNC     0603        1uF       6.3V             Ceramic cap.
- C4, C16, C20

CL10A106MQ8NNNC     0603        10uF      6.3V             Ceramic cap.
- C5, C6, C8, C10, C12, C14

ABLS-8.000MHZ-B4-T  HC-49/US    8MHz      CL 18pF          Crystal
- Y1  
````