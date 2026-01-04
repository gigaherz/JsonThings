$tellraw @a "$(Item) $(Count) $(User) $(Hand) $(RayX) $(RayY) $(RayZ) $(HitX) $(HitY) $(HitZ) $(HitFace) $(HitVX) $(HitVY) $(HitVZ) $(HitInside) $(HitEntity) $(Slot) $(Selected) $(OtherUser) $(TimeLeft) $(BlockX) $(BlockY) $(BlockZ) $(Attacker) $(Target) $(Block)"
$execute unless items entity @s $(Hand) *[count] run return 1
give @s apple 1
return -1