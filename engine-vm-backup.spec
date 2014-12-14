%define _version 1.0
%define _release 3

Name:		engine-vm-backup
Version:	%{_version}
Release:	%{_release}%{?dist}
Summary:	Engine auto vm backup service

Group:		ovirt-engine-third-party
License:	GPL
URL:		http://www.eayun.com
Source0:	engine-vm-backup-%{_version}.tar.gz
BuildRoot:	%(mktemp -ud %{_tmppath}/%{name}-%{version}-%{release}-XXXXXX)

BuildRequires:	/bin/bash
Requires:	ovirt-engine >= 3.5.0
Requires:	patternfly1

%description

%prep
%setup -q


%build
mvn clean package war:war


%install
rm -rf %{buildroot}
mkdir -p %{buildroot}/usr/share/ovirt-engine/ui-plugins/
mkdir -p %{buildroot}/usr/share/engine-vm-backup/deployments
mkdir -p %{buildroot}/etc/httpd/conf.d/
mkdir -p %{buildroot}/etc/engine-vm-backup/
mkdir -p %{buildroot}/usr/sbin/
mkdir -p %{buildroot}/etc/rc.d/init.d/
mkdir -p %{buildroot}/var/log/engine-vm-backup/
cp -r dist/UIPlugin/* %{buildroot}/usr/share/ovirt-engine/ui-plugins/
cp dist/config/z-vm-backup-scheduler-proxy.conf %{buildroot}/etc/httpd/conf.d/
cp dist/config/engine-vm-backup.xml %{buildroot}/etc/engine-vm-backup/
cp target/vmBackupScheduler.war %{buildroot}/usr/share/engine-vm-backup/deployments/
cp dist/bin/vm-backup-setup %{buildroot}/usr/sbin/
cp dist/bin/vm-backup-cleanup %{buildroot}/usr/sbin/
cp dist/service/engine-vm-backup %{buildroot}/etc/rc.d/init.d/
cp dist/dbscripts/createdb.sql %{buildroot}/usr/share/engine-vm-backup/
cp dist/config/engine-vm-backup.properties %{buildroot}/etc/engine-vm-backup/
touch %{buildroot}/etc/engine-vm-backup/mgmt-users.properties
touch %{buildroot}/etc/engine-vm-backup/application-users.properties

%clean
rm -rf %{buildroot}

%post
ln -s /usr/share/patternfly1/resources/ /usr/share/ovirt-engine/ui-plugins/vbsplugin-resources/patternfly


%files
%defattr(-,root,root,-)
%dir /etc/httpd/conf.d/
%config /etc/httpd/conf.d/z-vm-backup-scheduler-proxy.conf
%attr(0755,root,root) /usr/sbin/vm-backup-setup
%attr(0755,root,root) /usr/sbin/vm-backup-cleanup
%attr(0755,root,root) /etc/rc.d/init.d/engine-vm-backup
/usr/share/ovirt-engine/ui-plugins/
/usr/share/engine-vm-backup/
/etc/engine-vm-backup/
/var/log/engine-vm-backup/

%postun
unlink /usr/share/ovirt-engine/ui-plugins/vbsplugin-resources/patternfly


%changelog

* Fri Nov 21 2014 MaZhe <liyang.pan@eayun.com> 1.0-1
- First build

