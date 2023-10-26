
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