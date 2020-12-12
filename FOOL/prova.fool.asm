push 0
push 5
push 2
add
pushfunction0
lfp
push 1
lfp
stm
ltm
ltm
push-3
add
lw
js
push 1
beq label12
push 10
b label13
label12:
lfp
push-2
add
lw
label13:
print
halt

function1:
lfp
sra
lfp
push2
add
lw
lfp
push 3
lfp
lw
lw
push-2
add
lw
beq label4
push 0
b label5
label4:
push 1
label5:
lfp
lw
lw
stm
ltm
ltm
push-3
add
lw
js
stm
pop
pop
lra
ltm
lra

function0:
lfp
sra
pushfunction1
lfp
push1
add
lw
push 1
beq label10
push 0
b label11
label10:
lfp
push 3
push 2
lfp
stm
ltm
ltm
push-2
add
lw
js
label11:
stm
pop
pop
lra
ltm
lra