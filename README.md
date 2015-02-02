vm-backup-scheduler
===================

This is a service for auto backup ovirt-engine's vms via snapshot&export domain

build & install
===============

* $ make
* $ cp [tar.gz] ~/rpmbuild/SOURCE/
* $ rpmbuild -ba engine-vm-backup.spec
* install the rpm under ~/rpmbuild/RPMS/[arch] into the same machine of the
  installed and setup-complete ovirt-engine
* to setup vm-backup-scheduler, run: vm-backup-setup
* to cleanup vm-backup-scheduler, run: vm-backup-cleanup

Features
========

Each vm can have two policies, they can be setup under vm's "VM Backup" subtab
 * snapshot policy: when to execute snapshot, and how many snapshots is stored.
   When the snapshot action is triggered by vm's policy, a new new snapshot is
   created and can be seen under snapshots subtab.
   When snapshots created by vm-backup-scheduler has exceeded the policy's
   limitation, deletion is executed atomatically when vm is down (as live merge
   is platform dependent feature).
 * export policy: when to execute export, and how many export vm is stored.
   When the export action is triggered by vm's policy, vm-backup-scheduler will
   first make a clone of the vm, then export it into the export domain.
   If the vm is running during the action, make a snapshot of the vm and then
   clone a vm for export from that snapshot, after vm is down, the temp
   snapshot is auto deleted.
   The deletion of the export backup is similar as the snapshot policy.

And all the enabled vm policies can be listed under "VM Backup" main tab
