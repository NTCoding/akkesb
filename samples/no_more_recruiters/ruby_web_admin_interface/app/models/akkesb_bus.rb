require 'singleton'
require 'dbus'

class AkkesbBus
	include Singleton
	@bus

	def initialize
		@bus = DBus::SessionBus.instance
		@bus.request_service("akkesb.NoMoreRecruiters.RubyWebApp.Client")
	end

	def send(command, keys, values)
		host = @bus.service("akkesb.NoMoreRecruiters.RubyWebApp.Host").object("/messages/outgoing")
		host.introspect
		host_iface = host["akkesb.dbus.MessageSender"]
		host_iface.send(command, keys, values)
	end

end