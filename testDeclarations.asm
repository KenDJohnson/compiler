.data
foo: .word 0
bar: .word 0
foobar: .word 0
newline: .asciiz "\n"

.text
main:
 addi $sp, $sp, -4
 sw $ra, 0($sp)
 addi $t0, $zero, 5
 sw $t0, foo
 addi $t0, $zero, 2
 sw $t0, bar
 lw $t0, bar
 lw $t1, foo
 slt $t0, $t0, $t1
 beq $t0, $zero, if0false
 addi $t0, $zero, 0
 sw $t0, foo
 j if0end
 if0false:
 addi $t0, $zero, 20
 sw $t0, foo
 if0end:
 lw $a0, bar
 li $v0, 1
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 lw $a0, foo
 li $v0, 1
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 lw $t0, foo
 lw $t1, bar
 mult $t0, $t1
 mflo $t0
 li $t1, 6
 li $t2, 3
 div $t1, $t2
 mflo $t1
 add $t0, $t0, $t1
 li $t1, 10
 lw $t2, bar
 mult $t1, $t2
 mflo $t1
 add $t0, $t0, $t1
 sw $t0, foobar
 lw $a0, foobar
 li $v0, 1
 syscall
 li $v0, 4
 la $a0, newline
 syscall
 lw $ra, 0($sp)
 addi $sp, $sp, 4
 jr $ra
 