-- SimpleActor's types
NONE = "NONE"
DROP = "DROP"
GROUND = "GROUND"
CLOUD = "CLOUD"
EMITTER = "EMITTER"
HOUSE = "HOUSE"
HAMMER = "HAMMER"
BALL = "BALL"
TRIGGER = "TRIGGER"
-- BodyType
STATIC_BODY =0
KINEMATIC_BODY = 1
DYNAMIC_BODY = 2
-- Game Classes
Drop = luajava.bindClass("com.alex.rain.models.Drop")
Ground = luajava.bindClass("com.alex.rain.models.Ground")
House = luajava.bindClass("com.alex.rain.models.House")
Hammer = luajava.bindClass("com.alex.rain.models.Hammer")
Cloud = luajava.bindClass("com.alex.rain.models.Cloud")
Emitter = luajava.bindClass("com.alex.rain.models.Emitter")
Ball = luajava.bindClass("com.alex.rain.models.Ball")
Trigger = luajava.bindClass("com.alex.rain.models.Trigger")
-- System Classes
Vector2 = luajava.bindClass("com.badlogic.gdx.math.Vector2")

Color = luajava.bindClass("com.badlogic.gdx.graphics.Color")
Array = luajava.bindClass("java.lang.reflect.Array")

IEventListener = 'com.badlogic.gdx.scenes.scene2d.EventListener'