---
title: "Installing NixOS: Experiences in declarative Linux configuration"
date: 2014-11-07
tags: [linux, configuration]
---

<p>
I recently learned of a Linux distribution called <a href="http://nixos.org/">NixOS</a>.  Its website
describes it as a "purely functional" Linux distribution.  Since I'm a
fan of functional programming, the concept of a functional Linux
distribution intrigued me.  Even though I was fairly happy with my
<a href="http://archlinux.org/">Arch Linux</a> install, I decided to give NixOS a shot to see if it lived
up to its promise.  The bottom line is that NixOS and the Nix package
manager are fantastic but a few issues made me switch back to Arch
temporarily until I can get them sorted out.
</p>

<p>
I spent 2 days experimenting with NixOS on a 2013 MacBook Pro 15" and
later a 2012 MacBook Pro 13". The plan was to get a working
installation going on the first laptop and then generalize my
configuration so that I could install an almost identical
configuration on the other.
</p>

<p>
The first big thing that surprised me:
</p>

<div id="outline-container-unnumbered-1" class="outline-2">
<h2 id="unnumbered-1">The NixOS installation process is very smooth.</h2>
<div class="outline-text-2" id="text-unnumbered-1">
<p>
I started by booting up the NixOS live CD with the manual loaded up on
the other machine.  The first thing that impressed me was that the
NixOS manual is built into the live CD and accessible via console web
browser in one of the virtual terminals.  This is extremely helpful;
you don't need to have another device around to check the manual in
case you need to know how to configure something.  Arch Linux
developers should take note of this feature.
</p>

<p>
I didn't need to configure much, though.  The NixOS installation had
no problem identifying that I needed drivers for the NVIDIA graphics
chipset and a Broadcom wireless adapter. The
hardware-configuration.nix file was already set up for me so I didn't
have to do anything extra. My first configuration file additions were
merely to add a small list of packages I knew I would need. After
running nixos-install I rebooted into a working base system.
</p>

<p>
The next day I factored out some common configuration for my desktop
machines and kicked off an installation on the second MacBook.  I had
a little bit more trouble this time but only because I did a couple of
things wrong while getting creative with the installation steps.
After the base system was installed and I was able to access my
<a href="http://github.com/daviwil/nix-config">nix-config GitHub repository</a>, I installed the shared configuration on
the second machine with minor tweaks.
</p>

<p>
My experience with factoring out configuration code into shared files
made me a believer:
</p>
</div>
</div>


<div id="outline-container-unnumbered-2" class="outline-2">
<h2 id="unnumbered-2">Configuring your whole system with a single declarative language is really convenient and enjoyable.</h2>
<div class="outline-text-2" id="text-unnumbered-2">
<p>
While I was expanding and refactoring my configuration files, I
learned that Nix allows you to break up your configuration however you
like.  The initial two configuration files the installer generates for
you demonstrate this: configuration.nix <i>includes</i>
hardware-configuration.nix as a module.  Nothing stops you from using
this same mechanism to
<a href="http://nixos.org/nixos/manual/#sec-modularity">split up your common
configuration bits into seperate files</a>.
</p>

<p>
The more important discovery here was that Nix cleanly merges the
configuration settings from these module files without extra effort.
You can define a common configuration module for some subsystem (like
the desktop environment) and then override some of these settings in a
higher-level config file just by assigning a new value to the
necessary variable.  If one of my machines isn't playing nice with a
newer Linux kernel, I can change its configuration file to use an
older kernel until the bugs get worked out.  The rest of the shared
configuration remains untouched.  I plan to use this feature to create
configuration "mixin" modules for common things that are needed on
desktop and server installs.
</p>

<p>
<a href="http://nixos.org/nix/manual/#chap-writing-nix-expressions">Nix's expression language</a> is pretty rich and appears to provide a
relatively powerful functional language implementation.  It conveys
complex configurations concisely with a relatively low <a href="http://nbviewer.ipython.org/github/vanzaj/pyconsg2014-tutorial-cli/blob/master/ipynb/img/wtf-per-min.png">WTFs per minute</a>
factor.  One could certainly wonder why it was necessary to invent a
brand new language for this purpose when other perfectly good
functional languages exist.  When you compare Nix's lack of
heavyweight runtime to those of Haskell, OCaml or Clojure, it makes
sense why you would want a more specialized language environment for
configuration and deployment.
</p>

<p>
Building your configuration with this language is a lot of fun, but at
some point your machine will break.  Nix comes to the rescue:
</p>
</div>
</div>


<div id="outline-container-unnumbered-3" class="outline-2">
<h2 id="unnumbered-3">The ability to roll back to a previous working configuration is a lifesaver.</h2>
<div class="outline-text-2" id="text-unnumbered-3">
<p>
When I started to experiment with Bumblebee and disabled the NVIDIA
adapter in the 15" MacBook, I caused an issue where I couldn't see
anything on the screen after rebooting.  I had a similar problem when
I tried to use the new 3.17 kernel with the nouveau driver; it caused
consistent kernel panics at boot time.  This was purely due to the
eccentricities of the MacBook's hybrid graphics architecture and my
inexperience in dealing with it.  When I broke something like this,
NixOS allowed me to boot a previous working configuration so I had a
chance to fix the problem.
</p>

<p>
However, I had to go set the bootloader timeout to a non-zero value
before I could get to this menu. It wasn't set in the default
configuration.  When I realized this and tried to set the timeout
value I incorrectly set it for GRUB instead of Gummiboot. This
lead to some minor annoyance until I figured out what I had done. I
don't really blame this on NixOS though, just my lack of reading the
configuration text and understanding the implications of what I had
written.
</p>

<p>
After using my fresh installations for a day or so, I came to a conclusion:
</p>
</div>
</div>


<div id="outline-container-unnumbered-4" class="outline-2">
<h2 id="unnumbered-4">Some things just didn't work as well as they did on Arch.</h2>
<div class="outline-text-2" id="text-unnumbered-4">
<p>
The main issue I ran into on the 15" MacBook is that I could never
figure out how to tweak the backlight brightness on my NVIDIA adapter.
I got some pretty massive eye strain from staring at a fully
illuminated panel for hours that day.  <a href="https://github.com/NixOS/nixpkgs/tree/master/pkgs/os-specific/linux/nvidiabl">NixOS has the nvidiabl package</a>,
but it didn't work for me for some reason.  I'm sure someone out there
can coach me on the right configuration for this, though.  The
backlight brightness control worked fine for me in Arch Linux.
</p>

<p>
I also ran into a really bizarre issue with <a href="http://i3wm.org/">i3 window manager</a>: somehow
the screen would never redraw when the visible windows were changing.
I could only get the screen to refresh if I fullscreened an app.  I'm
not sure how the NixOS environment could have caused this but I've
never seen it happen on an Arch install.  Granted, this could totally
have been my fault by configuring something incorrectly.  I know that
<a href="http://twitter.com/ielectric">Domen Kožar</a> uses i3 in his NixOS environment, so the package must not
be broken.
</p>

<p>
Lastly, I found a few packages that just failed to install or build
for various reasons (pommed and cinelerra come to mind).  Some of
these packages just seemed to be unmaintained, possibly because the
upstream project hasn't changed in a while or people moved on to
better solutions (which weren't obvious if this was the case).  I'm
hoping that the nixpkgs repository maintainers are responsive to pull
requests because I'd like to help fix any broken packages I come
across.
</p>

<p>
However, I was pleasantly surprised that compared to Arch, a few
more obscure packages I wanted to install actually had official
binaries.  When I wanted to install <a href="https://github.com/stumpwm/stumpwm">StumpWM</a> on Arch, I had to build
an AUR package and do some other configuration manually.  On NixOS
I was able to find and install a StumpWM package with no trouble
(though I never did figure out how to get the stumpwm-contrib
libraries to load).
</p>

<p>
While there many binary packages ready to install:
</p>
</div>
</div>


<div id="outline-container-unnumbered-5" class="outline-2">
<h2 id="unnumbered-5">You have to dig around a lot in the <a href="http://github.com/NixOS/nixpkgs">NixOS/nixpkgs</a> repository.</h2>
<div class="outline-text-2" id="text-unnumbered-5">
<p>
If you want to install and configure certain packages (especially
those with server components) you have to search the nixpkgs
repository and figure out how to reference the desired package in your
.nix file.  This isn't so hard in most cases since you can just look
in the <a href="https://github.com/NixOS/nixpkgs/blob/master/pkgs/top-level/all-packages.nix">all-packages.nix</a> file (or others in the same folder) to find
references to most of them.  I did find that the syntax got
<a href="https://nixos.org/wiki/Haskell#System-wide_use_via_NixOS_config">a
little more complicated</a> when I needed to refer to Haskell packages,
but it makes sense once you start to understand Nix.
</p>

<p>
With that said, I don't necessarily have a problem with doing this.
The files in this repository regularly left me impressed with how good
the Nix language is for its purpose.  The package code is often pretty
self-documented, so it's not that hard to figure out what
configuration options you have for a given package.  There's also the
ability to <a href="https://nixos.org/wiki/Nix_Modifying_Packages#Overriding_Existing_Packages">override any variable</a> in a package for maximum
customization.  If I don't like the way a package configures its
software I can just override the appropriate variable even if no
standard option was provided.
</p>

<p>
Even though I ran into a few issues, I feel certain about one thing:
</p>
</div>
</div>


<div id="outline-container-unnumbered-6" class="outline-2">
<h2 id="unnumbered-6">I really want NixOS to be my go-to distro for desktop and server.</h2>
<div class="outline-text-2" id="text-unnumbered-6">
<p>
Although I reinstalled Arch on my 13" MacBook, I fully intend to keep
trying to tweak my NixOS configuration on the 15" MacBook to perform
well enough for daily driver use. Since I'm participating in <a href="http://itch.io/jam/procjam">ProcJam
2014</a> next week, I just wanted to make sure that one of my machines
would work bearably while the jam is going on.  I hope to help fix
some of the problems that I came across after the jam is complete.
</p>

<p>
I'm also thinking ahead about how I can use <a href="http://nixos.org/nixops/">NixOps</a> to manage and
deploy servers for private use or for projects that require a
distributed architecture.  It seems that <a href="http://nixos.org/nixops/manual/#ex-physical-multi-ec2.nix">AWS is very well supported</a> by
NixOps and there are already <a href="https://github.com/NixOS/nixops/blob/master/nix/ec2-amis.nix">quite a few NixOS AMI images</a> available
there.  You can even test out your configuration by deploying local
Virtualbox VMs.  Who needs Vagrant, Puppet/Chef, or Ansible when you
can solve all your configuration, testing, and deployment needs with a
solution integrated into the OS?  If you want to <a href="http://zef.me/6049/nix-docker/">build Docker container
images</a>, NixOS would be a really convenient automation tool.
</p>

<p>
I've checked my .nix files into a <a href="http://github.com/daviwil/nix-config">GitHub repository</a>.  Hopefully they
will be useful to someone who wants to give this underappreciated
distro a shot.
</p>
</div>
</div>
