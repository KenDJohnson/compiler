.data
foo: .word 0
bar: .word 0
foobar: .float 0.0
pi: .float 0.0
newline: .asciiz "\n"

.text
main:
 addi $sp, $sp, -4
 sw $ra, 0($sp)
 li $t0, 2
 sw $t0, foo
 li $t0, 10
 sw $t0, bar
 li.s $f0, 3.1415
 swc1 $f0, pi
 lwc1 $f0, pi
 li.s $f1, 2.0
 div.s $f0, $f0, $f1
 swc1 $f0, foobar
 lwc1 $f0, foobar
 lwc1 $f1, pi
 c.lt.s $f0, $f1
 bc1f if0false
 lwc1 $f12, foobar
 li $v0, 2
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 j if0end
 if0false:
 if0end:
 lwc1 $f0, foobar
 lwc1 $f1, pi
 c.lt.s $f1, $f0
 bc1f if1false
 j if1end
 if1false:
 lwc1 $f12, pi
 li $v0, 2
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 if1end:
 li $t0, 2
 li $t1, 3
 sne $t0, $t0, $t1
 beq $t0, $zero, if2false
 li $a0, 5
 li $v0, 1
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 j if2end
 if2false:
 if2end:
 li $t0, 2
 li $t1, 2
 add $t0, $t0, $t1
 li $t1, 4
 seq $t0, $t0, $t1
 beq $t0, $zero, if3false
 li $a0, 4
 li $v0, 1
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 j if3end
 if3false:
 if3end:
 li $t0, 2
 li $t1, 2
 add $t0, $t0, $t1
 li $t1, 4
 seq $t0, $t0, $t1
 beq $t0, $zero, if4false
 li $a0, 4
 li $v0, 1
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 j if4end
 if4false:
 li $a0, 5
 li $v0, 1
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 if4end:
 li.s $f0, 2.0
 lwc1 $f1, pi
 mul.s $f0, $f0, $f1
 li.s $f1, 2.0
 li.s $f2, 17.0
 div.s $f1, $f1, $f2
 add.s $f0, $f0, $f1
 swc1 $f0, foobar
 lwc1 $f12, foobar
 li $v0, 2
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 while0begin:
 lw $t7, bar
 li $t8, 0
 slt $t7, $t8, $t7
 beq $t7, $zero, while0end
 lw $t0, bar
 li $t1, 1
 sub $t0, $t0, $t1
 sw $t0, bar
 lw $a0, bar
 li $v0, 1
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 j while0begin
 while0end:
 lw $ra, 0($sp)
 addi $sp, $sp, 4
 jr $ra
 