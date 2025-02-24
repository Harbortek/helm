import user from './user'
import org from './org'
import role from './role'
import param from './param'
import Enum from './enum'
import log from './log'
import system from './system'

export default {
    ...user,
    ...org,
    ...role,
    ...param,
    ...Enum,
    ...log,
    ...system
}

