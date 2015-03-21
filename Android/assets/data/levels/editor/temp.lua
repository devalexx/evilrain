function addObjects(world)
    ------------------
    obj = luajava.new(Ground)
    obj:addVertex(624.0, 39.999996)
    obj:addVertex(624.0, 128.0)
    obj:addVertex(576.0, 128.0)
    obj:addVertex(576.0, 88.0)
    obj:addVertex(496.0, 88.0)
    obj:addVertex(496.0, 128.0)
    obj:addVertex(456.0, 128.0)
    obj:addVertex(456.0, 88.0)
    obj:addVertex(384.0, 88.0)
    obj:addVertex(384.0, 128.0)
    obj:addVertex(336.0, 128.0)
    obj:addVertex(336.0, 88.0)
    obj:addVertex(264.0, 88.0)
    obj:addVertex(264.0, 128.0)
    obj:addVertex(224.0, 128.0)
    obj:addVertex(224.0, 88.0)
    obj:addVertex(152.0, 88.0)
    obj:addVertex(152.0, 128.0)
    obj:addVertex(96.0, 128.0)
    obj:addVertex(96.0, 39.999996)
    obj:setPosition(0.0, 0.0)
    obj:setRotation(0.0)
    obj:setVisible(true)
    world:addActor(obj)

    ------------------
    obj = luajava.new(Cloud)
    obj:setPosition(700.0, 469.99997)
    obj:setRotation(0.0)
    obj:setVisible(true)
    world:addActor(obj)

    ------------------
    obj = luajava.new(Trigger)
    obj:setPosition(222.0, 152.43327)
    obj:setRotation(8.847365E-6)
    obj:setVisible(true)
    world:addActor(obj)

    ------------------
    obj = luajava.new(Trigger)
    obj:setPosition(332.0001, 152.43327)
    obj:setRotation(8.129439E-6)
    obj:setVisible(true)
    world:addActor(obj)

    ------------------
    obj = luajava.new(Trigger)
    obj:setPosition(454.0202, 152.59962)
    obj:setRotation(-0.2987837)
    obj:setVisible(true)
    world:addActor(obj)

    ------------------
    obj = luajava.new(Trigger)
    obj:setPosition(568.0002, 152.43327)
    obj:setRotation(6.789443E-6)
    obj:setVisible(true)
    world:addActor(obj)

end
function onCreate(world)
    addObjects(world)
end
function onBeginContact(contact)
end
function onEndContact(contact)
end
function onCheck(world, dropsCount)
end