-- SimpleActor's types
NONE = "NONE"
DROP = "DROP"
GROUND = "GROUND"
-- Game Classes
Drop = luajava.bindClass("com.alex.rain.models.Drop")
Ground = luajava.bindClass("com.alex.rain.models.Ground")
Home = luajava.bindClass("com.alex.rain.models.Home")
Hammer = luajava.bindClass("com.alex.rain.models.Hammer")
Cloud = luajava.bindClass("com.alex.rain.models.Cloud")
Emitter = luajava.bindClass("com.alex.rain.models.Emitter")
-- System Classes
Vector2 = luajava.bindClass("com.badlogic.gdx.math.Vector2")

Color = luajava.bindClass("com.badlogic.gdx.graphics.Color")
Array = luajava.bindClass("java.lang.reflect.Array")