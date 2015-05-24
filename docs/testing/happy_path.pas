program happyPath ;
var foo, bar : integer ;
var foobar, pi : real ;
begin
	foo := 2;
	bar := 10;
	pi := 3.1415 ;
	foobar := pi / 2 ;
	if foobar < pi then
		write(foobar)
	else
		begin
		end
	if foobar > pi then
		begin
		end
	else
		begin
			write(pi)
		end
	if 2 <> 3 then
		write(5)
	else
		begin
		end
	if 2 + 2 = 4 then
		write(4)
	else
		begin
		end
	if not (2 + 2 = 4) then
		write(4)
	else
		begin
		write(5)
		end
	foobar := 2*pi + 2 / 17 * 2.7183;
	write(foobar);
	while bar > 0 do
	begin
		bar := bar - 1;
		write(bar)
	end
	

end .

