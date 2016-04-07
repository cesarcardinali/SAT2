#!/usr/bin/perl
#
# (c) Motorola Mobility 2010
#
# Description:
#   Parses a kernel log and outputs a new file containing real time stamps based on the kernel
#   tick count and real time stamps found in the output.  The following are also analyzed in the
#   script:
#     Wakeups - A list of all of the wakeups durations and wakeup reasons is generated
#               at the bottom of the output file.
#
# Usage:
#   parse_bugreport.pl [--debug] <infile> <outfile>
#
# Where:
#   --debug   Causes debug output.
#   <infile>  is the name of the required input file.
#   <outfile> is the name of the required output file.
#
use warnings;
use integer;
use Getopt::Long;
use Time::Local;

my $BATTERY = #bat_cap#;
my $ALL_WL = 'ALL_KERNEL_WAKELOCKS';
my $BG_WL = 'BACKGROUND_KERNEL_WAKELOCKS';
my $STATS_UNPLUG = 'Statistics since last unplugged';
my $STATS_CHARGE = 'Statistics since last charge';
my $debug = 0;
my $start_pr = 0;
my $kernel_wl_ttime = 0;
my $kernel_wl_ttime_ns = 0;
my %kernel_wl;
my @java_wl = 0;
my $java_wl_c = 0;
my @java_wl_up = 0;
my $java_wl_up_c = 0;
my @krnl_wl = 0;
my $krnl_wl_c = 0;
my @krnl_wl_up = 0;
my $krnl_wl_up_c = 0;
my $java_unplugged = 0;
my $screen_on = 0;
my $time_on_batt = 0;
my $total_time = 0;
my $parse_procrank = 0;

sub debug_printf
{
    if ($debug)
    {
	printf @_;
    }
}

sub get_time_s {
    my ($str) = @_;
    my $s = 0;

    if ($str =~ /^(\d+)d (\d+)h (\d+)m (\d+)s (\d+)ms/)
    {
        $s = $1*86400+$2*3600+$3*60+$4+$5/1000;
    }
    if ($str =~ /^(\d+)h (\d+)m (\d+)s (\d+)ms/)
    {
        $s = $1*3600+$2*60+$3+$4/1000;
    }
    elsif ($str =~ /^(\d+)m (\d+)s (\d+)ms/)
    {
        $s = $1*60+$2+$3/1000;
    }
    elsif ($str =~ /^(\d+)s (\d+)ms/)
    {
        $s = $1+$2/1000;
    }
    elsif ($str =~ /^(\d+)ms/)
    {
        $s = $1/1000;
    }
    debug_printf "SCREEN ON $str $s\n";
    return $s;
}

sub total_wl {
    my ($wl, $count, $ttime, $ttime_ns, $mtime) = @_;

    $kernel_wl{$ALL_WL}{'count'} += $count;
    $kernel_wl{$ALL_WL}{'ttime'} += $ttime;
    $kernel_wl_ttime_ns += $ttime_ns;
    if ($kernel_wl{$ALL_WL}{'mtime'} < $mtime)
    {
        $kernel_wl{$ALL_WL}{'mtime'} = $mtime;
    }
    if (($wl ne "\"main\"") && ($wl ne "\"usb\""))
    {
        debug_printf "$wl\n";
        $kernel_wl{$BG_WL}{'count'} += $count;
        $kernel_wl{$BG_WL}{'ttime'} += $ttime;
        $kernel_wl_ttime_ns += $ttime_ns;

        if ($kernel_wl{$BG_WL}{'mtime'} < $mtime)
        {
            $kernel_wl{$BG_WL}{'mtime'} = $mtime;
            debug_printf "$wl, $mtime, $ttime\n";
        }
    }
}

if (!GetOptions("debug"    => \$debug,
                ))
{
    die "Unable to parse options.\n";
}

if (scalar(@ARGV) == 2)
{
    $BATTERY = $ARGV[1];
}
elsif (scalar(@ARGV) != 1)
{
    die "Please provide an input file.\n";
}
my $input_file = $ARGV[0];

open (INFILE, "<$input_file") or die "Unable to open input file $input_file.\n";
while (<INFILE>)
{
    my $line = $_;

    $line =~ s/\s+$//;

    if ($start_pr == 1)
    {
        if ($line eq "")
        {
            print "\n";
            $start_pr = 0;
        }
        else
        {
            if ($line =~ /(Amount discharged while screen on:\s*)(\d*)/)
            {
                my $batt = $BATTERY*$2/100;
                if ($screen_on)
                {
                    printf("$1$2: %s mAh in %sh%sm => %3s mA average\n", $batt, $screen_on/3600, ($screen_on/60)%60, ($batt*3600/$screen_on));
                }
                else
                {
                    printf("$1$2: %s mAh in %sh%sm => %3s mA average\n", $batt, $screen_on/3600, ($screen_on/60)%60, 0);
                }
            }
            elsif ($line =~ /(Amount discharged while screen off:\s*)(\d*)/)
            {
                my $batt = $BATTERY*$2/100;
                my $t_off = ($time_on_batt-$screen_on);
                if ($t_off)
                {
                    printf("$1$2: %s mAh in %sh%sm => %3s mA average\n", $batt, $t_off/3600, ($t_off/60)%60, ($batt*3600/($t_off)));
                }
                else
                {
                    printf("$1$2: %s mAh in %sh%sm => %3s mA average\n", $batt, $t_off/3600, ($t_off/60)%60, 0);
                }
                printf("Full Charge Battery Capacity: $BATTERY\n");
            }
            elsif (($line =~ /Kernel Wake lock.* \d+h /) ||
                   ($line =~ /Kernel Wake lock.* \d+d /))
            {
                if ($java_unplugged == 0) {
                    $krnl_wl[$krnl_wl_c++] = $line;
                } else {
                    $krnl_wl_up[$krnl_wl_up_c++] = $line;
                }
            }
            elsif ($line =~ /Time on battery\: (.*) \(.*\) realtime\,.*/)
            { # Time on battery: 6h 28m 31s 184ms (9.0%) realtime,
                print("$line\n");
                $time_on_batt = get_time_s($1);
            }
            elsif ($line =~ /Screen on: (.*) \(.*\) .*\, Interactive.*/)
            { # Screen on: 55m 3s 155ms (14.2%),
                print("$line\n");
                $screen_on = get_time_s($1);
            }
            elsif ($line =~ /Total run time\:/)
            {
                print "$line\n";
                if ($total_time eq "charge") 
                {
                    $total_time = $line;
                }
            } 
            elsif (!($line =~ /Kernel Wake lock/))
            {
                print "$line\n";
            }
        }
    }
    elsif ($line =~ /($STATS_CHARGE\:)/)
    {
        printf "$line\n";
        $start_pr = 1;
        $total_time = "charge";
    }
    elsif ($line =~ /Device battery use since last full charge/)
    {
        printf "$line\n";
        $start_pr = 1;
    }
    elsif ($line =~ /$STATS_UNPLUG\:/)
    {
        printf "$line\n";
        $start_pr = 1;
        $java_unplugged = 1;
    }
    elsif (($line =~ /Device is currently unplugged/) || ($line =~ /Device is currently plugged/))
    {
        printf "$line\n";
        $start_pr = 1;
    }
    elsif ($line =~ /KERNEL WAKELOCKS/)
    {
        $kernel_wl{$ALL_WL}{'mtime'} = 0;
        $kernel_wl{$BG_WL}{'mtime'} = 0;
    }
    elsif ($line =~ /(\".*\")\s+(\d+)\s+\d+\s+\d+\s+\d+\s+(\d+)(\d\d\d\d\d\d)\s+\d+\s+(\d+)\d\d\d\d\d\d\s+\d+/)
    {
        debug_printf "%16s %16s %d %d\n", $3, $5, $3, $5;
        if (($3 > 600000) || ($5 > 60000) || ($2 > 1000))
        {
            $kernel_wl{$1}{'count'} = $2;
            $kernel_wl{$1}{'ttime'} = $3;
            $kernel_wl{$1}{'mtime'} = $5;
        }
        total_wl($1, $2, $3, $4, $5);
    }
    elsif ($line =~ /(\".*\")\s+(\d+)\s+\d+\s+\d+\s+\d+\s+(\d+)(\d\d\d\d\d\d)\s+\d+\s+(\d+)\s+\d+/)
    {
        debug_printf "%16s %16s %d %d\n", $3, $5, $3, $5;
        if (($3 > 600000) || ($2 > 1000))
        {
            $kernel_wl{$1}{'count'} = $2;
            $kernel_wl{$1}{'ttime'} = $3;
            $kernel_wl{$1}{'mtime'} = $5/1000000;
        }
        total_wl($1, $2, $3, $4, $5/1000000);
    }
    elsif ($line =~ /(\".*\")\s+(\d+)\s+\d+\s+\d+\s+\d+\s+(\d+)\s+\d+\s+(\d+)\s+\d+/)
    {
        $kernel_wl_ttime_ns += $3
    }
    elsif (($line =~ /Wake lock.* \d+h /) ||
           ($line =~ /Wake lock.* \d+d /))
    {
        if ($java_unplugged == 0) {
            $java_wl[$java_wl_c++] = $line;
        } else {
            $java_wl_up[$java_wl_up_c++] = $line;
        }
    }
    elsif ($line =~ /Wake lock.* (\d+)m /)
    {
      if ($1 > 10) {
        if ($java_unplugged == 0) {
            $java_wl[$java_wl_c++] = $line;
        } else {
            $java_wl_up[$java_wl_up_c++] = $line;
        }
      }
    }
    elsif ($line =~ /PROCRANK/)
    {
        $parse_procrank = 1;
    }
    elsif ($line =~ /VIRTUAL MEMORY STATS/)
    {
        $parse_procrank = 0;
    }
    elsif ($parse_procrank == 1)
    {
        if ($line =~ /diag_mdlog/)
        {
             print "\n***************************************************\nBP_LOGGING ENABLED\n***************************************************\n\n";
#             exit;
        }
    }
}
close (INFILE);

#Add back in the $kernel_wl_ttime_ns
if ($kernel_wl_ttime != 0) {
  $kernel_wl{$ALL_WL}{'ttime'} += $kernel_wl_ttime_ns / 1000000;
  print "<DataBlockStart>Kernel wakelocks held</DataBlockStart>\n";
  print "Kernel wakelocks held > 10m total or > 1m at one time, out of (Total run time)\n";
  print "$total_time\n";
  print "==============================================================================\n";
  print "                               name   count    total_time     max_time\n";
  foreach my $wl (sort {$kernel_wl{$b}{'ttime'} <=> $kernel_wl{$a}{'ttime'}} keys %kernel_wl)
  {
  #    printf "%35s %7s %16s %16s\n", $wl, $kernel_wl{$wl}{'count'}, $kernel_wl{$wl}{'ttime'}/60000, $kernel_wl{$wl}{'mtime'}/60000;
      my $tt_h = $kernel_wl{$wl}{'ttime'}/3600000;
      my $tt_m = $kernel_wl{$wl}{'ttime'}/60000%60;
      my $tt_s = $kernel_wl{$wl}{'ttime'}/1000%60;
    if (defined($kernel_wl{$wl}{'mtime'})) {
      my $mt_h = $kernel_wl{$wl}{'mtime'}/3600000;
      my $mt_m = $kernel_wl{$wl}{'mtime'}/60000%60;
      my $mt_s = $kernel_wl{$wl}{'mtime'}/1000%60;
      printf "%35s %7s      %02d:%02d:%02d     %02d:%02d:%02d\n", $wl, $kernel_wl{$wl}{'count'}, $tt_h, $tt_m, $tt_s, $mt_h, $mt_m, $mt_s;
    }
  }
  print "<DataBlockEnd>Kernel wakelocks held</DataBlockEnd>\n";
}
print "\n<DataBlockStart>Java wakelocks held</DataBlockStart>\n";
print "$STATS_CHARGE\n";
for (my $java_wl_i = 0; $java_wl_i < $java_wl_c; $java_wl_i++)
{
    print "$java_wl[$java_wl_i]\n";
}
print "$STATS_UNPLUG\n";
for (my $java_wl_up_i = 0; $java_wl_up_i < $java_wl_up_c; $java_wl_up_i++)
{
    print "$java_wl_up[$java_wl_up_i]\n";
}
print "<DataBlockEnd>Java wakelocks held</DataBlockEnd>\n";
if ($kernel_wl_ttime == 0) {
  print "\n<DataBlockStart>Kernel wakelocks held</DataBlockStart>\n";
  print "$STATS_CHARGE\n";
  for (my $krnl_wl_i = 0; $krnl_wl_i < $krnl_wl_c; $krnl_wl_i++)
  {
    print "$krnl_wl[$krnl_wl_i]\n";
  }
  print "$STATS_UNPLUG\n";
  for (my $krnl_wl_up_i = 0; $krnl_wl_up_i < $krnl_wl_up_c; $krnl_wl_up_i++)
  {
    print "$krnl_wl_up[$krnl_wl_up_i]\n";
  }
  print "<DataBlockEnd>Kernel wakelocks held</DataBlockEnd>\n";
}
