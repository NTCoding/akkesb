require 'singleton'
require 'dbus'

class AkkesbBus
	include Singleton

	def initialize
		bus = DBus::SessionBus.instance
		bus.request_service("akkesb.NoMoreRecruiters.RubyWebApp.Client")
	end

	def send(to, keys, values)
		puts "yay" 
	end

end